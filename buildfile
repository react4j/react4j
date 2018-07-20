require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/gwt'
require 'buildr/jacoco'
require 'buildr/top_level_generate_dir'

DAGGER_RUNTIME_DEPS = [:javax_inject, :dagger_core]
DAGGER_PROCESSOR_DEPS = [:javax_inject, :dagger_core, :dagger_spi, :dagger_producers, :dagger_compiler, :googlejavaformat, :errorprone, :javapoet, :guava]

EXAMPLES = {
  'hello_world' => 'react4j.examples.hello_world.HelloWorld'
}

desc 'React4j: An opinionated Java binding for React'
define 'react4j' do
  project.group = 'org.realityforge.react4j'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  desc 'React4j core binding'
  define 'core' do
    pom.include_transitive_dependencies << artifact(:javax_annotation)
    pom.include_transitive_dependencies << artifact(:jsinterop_base)
    pom.include_transitive_dependencies << artifact(:elemental2_core)
    pom.include_transitive_dependencies << artifact(:braincheck)
    pom.dependency_filter = Proc.new {|dep| dep[:id].to_s != 'jsinterop-annotations' && dep[:scope].to_s != 'test'}

    js_assets(project, :core)

    compile.with :javax_annotation,
                 :elemental2_core,
                 :jsinterop_base,
                 :jsinterop_annotations,
                 :braincheck

    gwt_enhance(project)

    test.using :testng
    test.options[:properties] = { 'react4j.core.compile_target' => compile.target.to_s }
    test.options[:java_args] = ['-ea']
    test.compile.with :jdepend

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'React4j DOM binding'
  define 'dom' do
    pom.include_transitive_dependencies << project('core').package(:jar)
    pom.include_transitive_dependencies << artifact(:elemental2_dom)
    pom.dependency_filter = Proc.new {|dep| !project('core').compile.dependencies.include?(dep[:artifact]) && dep[:id].to_s != 'elemental2-promise' && dep[:scope].to_s != 'test'}

    js_assets(project, :dom)

    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :elemental2_dom,
                 :elemental2_promise

    generate_factory_source(project)
    gwt_enhance(project)

    project.doc.options.merge!('Xdoclint:all,-missing' => true)

    test.using :testng
    test.options[:properties] = { 'react4j.dom.compile_target' => compile.target.to_s }
    test.options[:java_args] = ['-ea']
    test.compile.with :jdepend

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'React4j-Arez Integration'
  define 'arez' do
    pom.include_transitive_dependencies << project('dom').package(:jar)
    pom.include_transitive_dependencies << artifact(:arez_core)
    pom.include_transitive_dependencies << artifact(:arez_spytools)
    pom.dependency_filter = Proc.new {|dep| !project('dom').compile.dependencies.include?(dep[:artifact]) && (dep[:group].to_s != 'org.realityforge.arez' || dep[:id].to_s == 'arez-component') && dep[:group].to_s != 'org.jetbrains' && dep[:scope].to_s != 'test'}

    compile.with project('dom').package(:jar),
                 project('dom').compile.dependencies,
                 :arez_core,
                 :jetbrains_annotations,
                 :arez_spytools

    gwt_enhance(project)

    test.using :testng
    test.options[:properties] = { 'react4j.arez.compile_target' => compile.target.to_s }
    test.options[:java_args] = ['-ea']
    test.compile.with :jdepend

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'The Annotation processor'
  define 'processor' do
    pom.dependency_filter = Proc.new {|_| false}

    project.enable_annotation_processor = true

    compile.with :autoservice,
                 :autocommon,
                 :javapoet,
                 :guava,
                 :javax_annotation

    test.with :compile_testing,
              Java.tools_jar,
              :truth,
              project('arez').package(:jar),
              project('arez').compile.dependencies,
              :javax_inject,
              DAGGER_PROCESSOR_DEPS,
              :arez_processor,
              :gwt_user,
              :gwt_dev

    package(:jar)
    package(:sources)
    package(:javadoc)

    package(:jar).enhance do |jar|
      jar.merge(artifact(:javapoet))
      jar.merge(artifact(:guava))
      jar.merge(artifact(:autocommon))
      jar.enhance do |f|
        shaded_jar = (f.to_s + '-shaded')
        Buildr.ant 'shade_jar' do |ant|
          artifact = Buildr.artifact(:shade_task)
          artifact.invoke
          ant.taskdef :name => 'shade', :classname => 'org.realityforge.ant.shade.Shade', :classpath => artifact.to_s
          ant.shade :jar => f.to_s, :uberJar => shaded_jar do
            ant.relocation :pattern => 'com.squareup.javapoet', :shadedPattern => 'react4j.processor.vendor.javapoet'
            ant.relocation :pattern => 'com.google', :shadedPattern => 'react4j.processor.vendor.google'
          end
        end
        FileUtils.mv shaded_jar, f.to_s
      end
    end

    test.using :testng
    test.options[:properties] = { 'react4j.fixture_dir' => _('src/test/resources') }
    test.options[:java_args] = ['-ea']

    iml.test_source_directories << _('src/test/resources/input')
    iml.test_source_directories << _('src/test/resources/expected')
    iml.test_source_directories << _('src/test/resources/bad_input')
  end

  desc 'Utilities to output of GWT when compiling React4j applications'
  define 'gwt-output-qa' do
    pom.include_transitive_dependencies << artifact(:gwt_symbolmap)
    pom.dependency_filter = Proc.new {|dep| %w(org.realityforge.gwt.symbolmap).include?(dep[:group].to_s)}

    compile.with :javax_annotation,
                 :javacsv,
                 :gwt_symbolmap,
                 :testng

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'Test React4j in downstream projects'
  define 'downstream-test' do
    compile.with :gir,
                 :javax_annotation

    test.options[:properties] =
      {
        'react4j.current.version' => ENV['PREVIOUS_PRODUCT_VERSION'] || project.version,
        'react4j.next.version' => ENV['PRODUCT_VERSION'] || project.version,
        'react4j.deploy_test.fixture_dir' => _('src/test/resources/fixtures').to_s,
        'react4j.deploy_test.work_dir' => _(:target, 'deploy_test/workdir').to_s
      }
    test.options[:java_args] = ['-ea']

    local_test_repository_url = URI.join('file:///', project._(:target, :local_test_repository)).to_s
    compile.enhance do
      projects_to_upload = projects(%w(core processor arez dom))
      old_release_to = repositories.release_to
      begin
        # First we install them in a local repository so we don't have to access the network during local builds
        repositories.release_to = local_test_repository_url
        projects_to_upload.each do |prj|
          prj.packages.each do |pkg|
            # Uninstall version already present in local maven cache
            pkg.uninstall
            # Install version into local repository
            pkg.upload
          end
        end
        if ENV['STAGE_RELEASE'] == 'true'
          # Then we install it to a remote repository so that TravisCI can access the builds when it attempts
          # to perform a release
          repositories.release_to =
            { :url => 'https://stocksoftware.jfrog.io/stocksoftware/staging', :username => ENV['STAGING_USERNAME'], :password => ENV['STAGING_PASSWORD'] }
          projects_to_upload.each do |prj|
            prj.packages.each do |pkg|
              pkg.upload
            end
          end
        end
      ensure
        repositories.release_to = old_release_to
      end
    end unless ENV['TEST'] == 'no'

    test.compile.enhance do
      cp = project.compile.dependencies.map(&:to_s) + [project.compile.target.to_s]

      properties = {}
      # Take the version that we are releasing else fallback to project version
      properties['react4j.current.version'] = ENV['PREVIOUS_PRODUCT_VERSION'] || project.version
      properties['react4j.next.version'] = ENV['PRODUCT_VERSION'] || project.version
      properties['react4j.deploy_test.work_dir'] = _(:target, 'deploy_test/workdir').to_s
      properties['react4j.deploy_test.fixture_dir'] = _('src/test/resources/fixtures').to_s
      properties['react4j.deploy_test.local_repository_url'] = local_test_repository_url
      properties['react4j.deploy_test.store_statistics'] = ENV['STORE_BUILD_STATISTICS'] == 'true'

      Java::Commands.java 'react4j.downstream.BuildDownstream', { :classpath => cp, :properties => properties } unless ENV['DOWNSTREAM'] == 'no'
      Java::Commands.java 'react4j.downstream.CollectBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
    end

    # Only run this test when preparing for release, never on TravisCI (as produces different byte sizes)
    test.exclude '*BuildStatsTest' if ENV['PRODUCT_VERSION'].nil? || ENV['BUILD_STATS'] == 'no' || !ENV['TRAVIS_BUILD_NUMBER'].nil?
    test.exclude '*BuildOutputTest' if ENV['BUILD_STATS'] == 'no'

    test.using :testng
    test.compile.with :guiceyloops,
                      project('gwt-output-qa').package(:jar),
                      project('gwt-output-qa').compile.dependencies
  end

  desc 'Examples that are only used to illustrate ideas in documentation'
  define 'doc-examples' do
    project.enable_annotation_processor = true

    compile.with project('dom').package(:jar),
                 project('dom').compile.dependencies,
                 project('arez').package(:jar),
                 project('arez').compile.dependencies,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies,
                 DAGGER_PROCESSOR_DEPS,
                 :arez_browserlocation,
                 :arez_processor,
                 :gwt_user

    gwt_enhance(project, :modules_complete => true, :package_jars => false, :output_key => 'react4j-doc-examples')
  end

  doc.from(projects(%w(core dom arez processor))).
    using(:javadoc,
          :windowtitle => 'React4j API Documentation',
          :linksource => true,
          :timestamp => false,
          :link => %w(https://arez.github.io/api https://arez.github.io/spytools https://docs.oracle.com/javase/8/docs/api http://www.gwtproject.org/javadoc/latest/),
          :group => {
            'Core Packages' => 'react4j*',
            'DOM Packages' => 'react4j.dom*',
            'Annotation Packages' => 'react4j.annotations*:react4j.processor*',
            'Arez Packages' => 'react4j.arez*'
          }
    )

  iml.excluded_directories << project._('tmp')
  iml.excluded_directories << project._('node_modules')

  ipr.add_default_testng_configuration(:jvm_args => "-ea -Dbraincheck.environment=development -Dreact4j.environment=development -Dreact4j.output_fixture_data=false -Dreact4j.fixture_dir=processor/src/test/resources -Dreact4j.current.version=X -Dreact4j.next.version=X -Dreact4j.deploy_test.work_dir=#{project('downstream-test')._(:target, 'deploy_test/workdir')} -Dreact4j.deploy_test.fixture_dir=#{project('downstream-test')._('src/test/resources/fixtures')} -Dreact4j.deploy_test.local_repository_url=#{URI.join('file:///', project('downstream-test')._(:target, :local_test_repository))} -Dreact4j.deploy_test.store_statistics=false -Dreact4j.core.compile_target=target/react4j_core/idea/classes -Dreact4j.dom.compile_target=target/react4j_dom/idea/classes -Dreact4j.arez.compile_target=target/react4j_arez/idea/classes")
  ipr.add_component_from_artifact(:idea_codestyle)

  EXAMPLES.each_pair do |key, gwt_module|
    ipr.add_gwt_configuration(project('doc-examples'),
                              :name => "GWT: #{key}",
                              :gwt_module => gwt_module,
                              :start_javascript_debugger => false,
                              :vm_parameters => "-Xmx2G -Djava.io.tmpdir=#{_('tmp/gwt')}",
                              :shell_parameters => "-port 8888 -codeServerPort 8889 -bindAddress 0.0.0.0 -war #{_(:generated, 'gwt-export')}/")
  end
end

Buildr.projects.each do |project|
  project.jacoco.enabled = false unless project.name == 'react4j:processor'
end
