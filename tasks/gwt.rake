#
# Enhance the Buildr project to compile gwt sources.
# For each of the supplied gwt modules, this task will create
# a synthetic gwt module that includes a single entrypoint to
# compile against. It will also create a gwt classifier jar
# that includes all the sources.
#
def gwt_enhance(project, gwt_modules, options = {})
  modules_complete = !!options[:modules_complete]

  extra_deps = project.iml.main_generated_resource_directories.flatten.compact.collect do |a|
    a.is_a?(String) ? file(a) : a
  end + project.iml.main_generated_source_directories.flatten.compact.collect do |a|
    a.is_a?(String) ? file(a) : a
  end

  dependencies =
    project.compile.dependencies + [project.compile.target] + extra_deps + [Buildr.artifact(:gwt_user)]

  unless modules_complete
    base_synthetic_module_dir = project._(:generated, :synthetic_gwt_module, :main, :resources)
    t = project.task('gwt_synthetic_module') do
      gwt_modules.each do |gwt_module|
        file = "#{base_synthetic_module_dir}/#{gwt_module.gsub('.', '/')}Test.gwt.xml"
        mkdir_p File.dirname(file)
        IO.write(file, <<CONTENT)
<module>
  <inherits name="#{gwt_module}"/>
  <inherits name="com.google.gwt.user.User"/>
  <collapse-all-properties/>
</module>
CONTENT
      end
    end
    dir = project.file(base_synthetic_module_dir => [t.name])
    dependencies += [dir]
  end

  if ENV['GWT'].nil? || ENV['GWT'] == project.name
    modules = modules_complete ? gwt_modules : gwt_modules.collect {|gwt_module| "#{gwt_module}Test"}
    project.gwt(modules, { :java_args => %w(-Xms512M -Xmx1024M), :dependencies => dependencies })
  end

  project.package(:jar).tap do |j|
    extra_deps.each do |dep|
      j.include("#{dep}/*")
    end
    project.assets.paths.each do |path|
      j.include("#{path}/*")
    end
    j.include("#{project._(:source, :main, :java)}/*")
  end

  config = {}
  gwt_modules.each do |gwt_module|
    config[gwt_module] = false
  end
  project.iml.add_gwt_facet(config, :settings => {
    :compilerMaxHeapSize => '1024',
    :compilerParameters => '-draftCompile -localWorkers 2 -strict'
  }, :gwt_dev_artifact => :gwt_dev)
end
