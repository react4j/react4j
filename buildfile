require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/gwt'
require 'buildr/jacoco'

PROVIDED_DEPS = [:javax_jsr305, :jetbrains_annotations, :anodoc]

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

  desc 'Annotations for defining a react component'
  define 'annotations' do
    pom.provided_dependencies.concat PROVIDED_DEPS
    pom.dependency_filter = Proc.new {|dep| dep[:group].to_s != 'com.google.jsinterop' || (dep[:id].to_s == 'base' && dep[:classifier].nil?)}

    compile.with PROVIDED_DEPS,
                 :jsinterop_base,
                 :jsinterop_base_sources,
                 :jsinterop_annotations,
                 :jsinterop_annotations_sources

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'React4j core binding'
  define 'core' do
    pom.additional_dependencies << project('annotations').package(:jar)
    pom.include_transitive_dependencies << project('annotations').package(:jar)
    pom.dependency_filter = Proc.new {|dep| !project('annotations').compile.dependencies.include?(dep[:artifact])}

    js_assets(project, :core)

    compile.with PROVIDED_DEPS,
                 :elemental2_core,
                 :jsinterop_base,
                 :jsinterop_base_sources,
                 :jsinterop_annotations,
                 :jsinterop_annotations_sources,
                 :braincheck

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'React4j DOM binding'
  define 'dom' do
    pom.provided_dependencies.concat PROVIDED_DEPS
    pom.include_transitive_dependencies << project('core').package(:jar)
    pom.include_transitive_dependencies << artifact(:elemental2_dom)
    pom.dependency_filter = Proc.new {|dep| !project('core').compile.dependencies.include?(dep[:artifact]) && dep[:id].to_s != 'elemental2-promise'}

    js_assets(project, :dom)

    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :elemental2_dom,
                 :elemental2_promise

    generate_factory_source(project)
    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'React4j-Arez Integration'
  define 'arez' do
    pom.provided_dependencies.concat PROVIDED_DEPS
    pom.include_transitive_dependencies << project('dom').package(:jar)
    pom.include_transitive_dependencies << artifact(:arez_component)
    pom.dependency_filter = Proc.new {|dep| !project('dom').compile.dependencies.include?(dep[:artifact]) && (dep[:group].to_s != 'org.realityforge.arez' || dep[:id].to_s == 'arez-component') }

    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 project('dom').package(:jar),
                 project('dom').compile.dependencies,
                 :arez_core,
                 :arez_annotations,
                 :arez_component,
                 :arez_spytools

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'The Annotation processor'
  define 'processor' do
    pom.dependency_filter = Proc.new {|_| false }

    project.enable_annotation_processor = true

    compile.with :autoservice,
                 :autocommon,
                 :javapoet,
                 :guava,
                 :javax_jsr305

    test.with :compile_testing,
              Java.tools_jar,
              :truth,
              project('arez').package(:jar),
              project('arez').compile.dependencies,
              project('annotations').package(:jar),
              project('annotations').compile.dependencies,
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

    iml.test_source_directories << _('src/test/resources/input')
    iml.test_source_directories << _('src/test/resources/expected')
    iml.test_source_directories << _('src/test/resources/bad_input')
  end

  desc 'Utilities to output of GWT when compiling React4j applications'
  define 'gwt-output-qa' do
    compile.with PROVIDED_DEPS,
                 :javacsv,
                 :gwt_symbolmap,
                 :testng

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'Test React4j in downstream projects'
  define 'downstream-test' do
    compile.with :gir, PROVIDED_DEPS

    test.options[:properties] =
      {
        # Take the version that we are releasing else fallback to project version
        'react4j.version' => ENV['PRODUCT_VERSION'] || project.version,
        'react4j.deploy_test.fixture_dir' => _('src/test/resources/fixtures').to_s,
        'react4j.deploy_test.work_dir' => _(:target, 'deploy_test/workdir').to_s
      }
    test.options[:java_args] = ['-ea']

    local_test_repository_url = URI.join('file:///', project._(:target, :local_test_repository)).to_s
    compile.enhance do
      projects_to_upload =projects(%w(annotations core processor arez dom))
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
      properties['react4j.version'] = ENV['PRODUCT_VERSION'] || project.version
      properties['react4j.deploy_test.work_dir'] = _(:target, 'deploy_test/workdir').to_s
      properties['react4j.deploy_test.fixture_dir'] = _('src/test/resources/fixtures').to_s
      properties['react4j.deploy_test.local_repository_url'] = local_test_repository_url
      properties['react4j.deploy_test.store_statistics'] = ENV['STORE_BUILD_STATISTICS'] == 'true'

      Java::Commands.java 'react4j.downstream.BuildDownstream', { :classpath => cp, :properties => properties }
      Java::Commands.java 'react4j.downstream.CollectBuildStats', { :classpath => cp, :properties => properties }
    end

    # Only run this test when preparing for release
    test.exclude '*BuildStatsTest' unless ENV['PRODUCT_VERSION']

    test.using :testng
    test.compile.with :guiceyloops,
                      project('gwt-output-qa').package(:jar),
                      project('gwt-output-qa').compile.dependencies
  end

  desc 'Examples that are only used to illustrate ideas in documentation'
  define 'doc-examples' do
    project.enable_annotation_processor = true

    compile.with project('annotations').package(:jar),
                 project('annotations').compile.dependencies,
                 project('core').package(:jar),
                 project('core').compile.dependencies,
                 project('dom').package(:jar),
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

  doc.from(projects(%w(annotations core dom arez processor))).
    using(:javadoc,
          :windowtitle => 'React4j API Documentation',
          :linksource => true,
          :timestamp => false,
          :link => %w(https://arez.github.io/api https://arez.github.io/spytools https://docs.oracle.com/javase/8/docs/api http://www.gwtproject.org/javadoc/latest/),
          :group => {
            'Core Packages' => 'react4j.core*',
            'DOM Packages' => 'react4j.dom*',
            'Annotation Packages' => 'react4j.annotations*:react4j.processor*',
            'Arez Packages' => 'react4j.arez*'
          }
    )

  iml.excluded_directories << project._('tmp')
  iml.excluded_directories << project._('node_modules')

  ipr.add_default_testng_configuration(:jvm_args => "-ea -Dbraincheck.environment=development -Dreact4j.environment=development -Dreact4j.output_fixture_data=false -Dreact4j.fixture_dir=processor/src/test/resources -Dreact4j.version=#{ENV['PRODUCT_VERSION'] || project.version} -Dreact4j.deploy_test.work_dir=#{project('downstream-test')._(:target, 'deploy_test/workdir')} -Dreact4j.deploy_test.fixture_dir=#{project('downstream-test')._('src/test/resources/fixtures')} -Dreact4j.deploy_test.local_repository_url=#{URI.join('file:///', project('downstream-test')._(:target, :local_test_repository))} -Dreact4j.deploy_test.store_statistics=false")
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
