#
# Enhance the Buildr project to compile gwt sources.
# For each of the discovered gwt modules, this task will create
# a synthetic gwt module that includes a single entrypoint to
# compile against. It will also create a gwt classifier jar
# that includes all the sources.
#
def gwt_enhance(project, options = {})
  modules_complete = !!options[:modules_complete]
  package_jars = options[:package_jars].nil? ? true : !!options[:package_jars]

  extra_deps = (project.iml.main_generated_resource_directories.flatten + (options[:extra_deps] || [])).compact.collect do |a|
    a.is_a?(String) ? file(a) : a
  end + project.iml.main_generated_source_directories.flatten.compact.collect do |a|
    a.is_a?(String) ? file(a) : a
  end

  if !!project.compile.options[:processor] || (project.compile.options[:processor].nil? && !(project.compile.options[:processor_path] || []).empty?)
    extra_deps += [project.file(project._(:target, :generated, 'processors/main/java'))]
  end

  project.compile.with Buildr::GWT.dependencies(project.gwt_detect_version(Buildr.artifacts(:gwt_user)))

  dependencies = project.compile.dependencies + extra_deps

  gwt_modules = options[:gwt_modules] || []
  source_paths = project.compile.sources + project.iml.main_generated_resource_directories.flatten.compact + project.iml.main_generated_source_directories.flatten.compact
  source_paths.each do |base_dir|
    Dir["#{base_dir}/**/*.gwt.xml"].each do |filename|
      gwt_modules << filename.gsub("#{base_dir}/", '').gsub('.gwt.xml', '').gsub('/', '.')
    end
  end if gwt_modules.empty?

  compile_report_dir = options[:compile_report_dir]
  if modules_complete && compile_report_dir.nil?
    compile_report_dir = project._(:target, :gwt_compile_reports)
  end

  unless modules_complete
    base_synthetic_module_dir = project._(:generated, :synthetic_gwt_module, :main, :resources)
    t = project.task('gwt_synthetic_module') do
      gwt_modules.each do |gwt_module|
        file = "#{base_synthetic_module_dir}/#{gwt_module.gsub('.', '/')}Test.gwt.xml"
        mkdir_p File.dirname(file)
        IO.write(file, <<CONTENT)
<module>
  <inherits name='#{gwt_module}'/>
  <inherits name='com.google.gwt.user.User'/>
  <source path='ignored'/>
  <collapse-all-properties/>
</module>
CONTENT
      end
    end
    dir = project.file(base_synthetic_module_dir => [t.name])
    dependencies += [dir]
  end

  # Duplicate the assets as the following gwt task will add compile output to asset path
  # which we typically do NOT want to include in jar
  assets = project.assets.paths.dup
  if ENV['GWT'].nil? || ENV['GWT'] == project.name
    modules = modules_complete ? gwt_modules : gwt_modules.collect { |gwt_module| "#{gwt_module}Test" }
    modules.each do |m|
      gwtc_args = options[:module_gwtc_args].nil? ? nil : options[:module_gwtc_args][m]
      output_key = options[:output_key] || m
      project.gwt([m], { :java_args => %w(-Xms512M -Xmx1024M -Dgwt.watchFileChanges=false),
                         :dependencies => dependencies,
                         :gwtc_args => gwtc_args,
                         :skip_merge_gwt_dependencies => true,
                         :compile_report_dir => compile_report_dir.nil? ? nil : "#{compile_report_dir}/#{output_key}",
                         :output_key => output_key })
    end
  end

  project.package(:jar).tap do |j|
    extra_deps.each do |dep|
      j.enhance([dep]) do |j2|
        j2.include("#{dep}/*")
      end
    end
    assets.each do |path|
      j.include("#{path}/*")
    end
    j.include("#{project._(:source, :main, :java)}/*")
  end if package_jars

  config = {}
  gwt_modules.each do |gwt_module|
    config[gwt_module] = false
  end
  project.iml.add_gwt_facet(config, :settings => {
    :compilerMaxHeapSize => '1024',
    :compilerParameters => '-draftCompile -localWorkers 2 -strict'
  }, :gwt_dev_artifact => :gwt_dev)
end
