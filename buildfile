require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/gwt'
require 'buildr/jacoco'
require 'buildr/top_level_generate_dir'

DAGGER_RUNTIME_DEPS = [:javax_inject, :dagger_core]
DAGGER_PROCESSOR_DEPS =
  [
    :javax_inject,
    :dagger_core,
    :dagger_producers,
    :dagger_spi,
    :dagger_compiler,
    :guava_failureaccess,
    :kotlinx_metadata_jvm,
    :kotlin_stdlib,
    :kotlin_stdlib_common,
    :googlejavaformat,
    :errorprone,
    :javapoet,
    :guava
  ]

EXAMPLES = {
  'hello_world' => 'react4j.examples.hello_world.HelloWorld'
}

# JDK options passed to test environment. Essentially turns assertions on.
REACT_TEST_OPTIONS =
  {
    'braincheck.environment' => 'development',
    'arez.environment' => 'development',
    'react4j.environment' => 'development'
  }

desc 'React4j: An opinionated Java binding for React'
define 'react4j' do
  project.group = 'org.realityforge.react4j'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all,-processing,-serial'
  project.compile.options.warnings = true
  project.compile.options.other = %w(-Werror -Xmaxerrs 10000 -Xmaxwarns 10000)

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('react4j/react4j')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'React4j core binding'
  define 'core' do
    deps = artifacts(:javax_annotation, :jsinterop_annotations, :jsinterop_base, :jetbrains_annotations, :braincheck, :grim_annotations, :elemental2_promise, :elemental2_core, :arez_core)
    pom.include_transitive_dependencies << deps
    pom.dependency_filter = Proc.new { |dep| dep[:scope].to_s != 'test' && deps.include?(dep[:artifact]) }

    project.processorpath << artifacts(:grim_processor, :javax_json)

    js_assets(project, :core)

    compile.with deps

    gwt_enhance(project)

    test.using :testng
    test.options[:properties] =
      REACT_TEST_OPTIONS.merge('react4j.core.compile_target' => compile.target.to_s)
    test.options[:java_args] = ['-ea']
    test.compile.with :jdepend,
                      :mockito,
                      :arez_testng

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'React4j DOM binding'
  define 'dom' do
    deps = [project('core').package(:jar)] + artifacts(:elemental2_dom)
    pom.include_transitive_dependencies << deps
    pom.dependency_filter = Proc.new { |dep| dep[:scope].to_s != 'test' && deps.include?(dep[:artifact]) }

    project.processorpath << artifacts(:grim_processor, :javax_json)

    js_assets(project, :dom)

    compile.with deps,
                 project('core').compile.dependencies

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

  desc 'Test React4j API'
  define 'api-test' do
    test.compile.with :javax_annotation,
                      :javax_json,
                      :gir

    test.options[:properties] =
      {
        'react4j.api_test.store_api_diff' => ENV['STORE_API_DIFF'] == 'true',
        'react4j.prev.version' => ENV['PREVIOUS_PRODUCT_VERSION'],
        'react4j.prev.core.jar' => artifact("org.realityforge.react4j:react4j-core:jar:#{ENV['PREVIOUS_PRODUCT_VERSION'] || project.version}").to_s,
        'react4j.prev.dom.jar' => artifact("org.realityforge.react4j:react4j-dom:jar:#{ENV['PREVIOUS_PRODUCT_VERSION'] || project.version}").to_s,
        'react4j.next.version' => ENV['PRODUCT_VERSION'],
        'react4j.next.core.jar' => project('core').package(:jar).to_s,
        'react4j.next.dom.jar' => project('dom').package(:jar).to_s,
        'react4j.api_test.fixture_dir' => _('src/test/resources/fixtures').to_s,
        'react4j.revapi.jar' => artifact(:revapi_diff).to_s
      }
    test.options[:java_args] = ['-ea']
    test.using :testng

    test.compile.enhance do
      mkdir_p _('src/test/resources/fixtures')
      artifact("org.realityforge.react4j:react4j-core:jar:#{ENV['PREVIOUS_PRODUCT_VERSION']}").invoke
      artifact("org.realityforge.react4j:react4j-dom:jar:#{ENV['PREVIOUS_PRODUCT_VERSION']}").invoke
      project('core').package(:jar).invoke
      project('dom').package(:jar).invoke
      artifact(:revapi_diff).invoke
    end unless ENV['TEST'] == 'no' || ENV['PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'].nil?

    test.exclude '*ApiDiffTest' if ENV['PRODUCT_VERSION'].nil? || ENV['PREVIOUS_PRODUCT_VERSION'].nil?

    project.jacoco.enabled = false
  end

  desc 'The Annotation processor'
  define 'processor' do
    pom.dependency_filter = Proc.new { |_| false }

    # Note: It is deliberate that Sting annotation processor is not present otherwise fixtures
    # would not compile reliably due to the presence of @AutoFragment
    project.processorpath << [:arez_processor]

    compile.with :proton_core,
                 :javapoet,
                 :javax_annotation

    test.with :compile_testing,
              :guava,
              :proton_qa,
              :junit,
              :hamcrest_core,
              Java.tools_jar,
              :truth,
              project('core').package(:jar),
              project('core').compile.dependencies,
              :javax_inject,
              DAGGER_PROCESSOR_DEPS,
              :arez_processor,
              :sting_core,
              :sting_processor,
              :gwt_user,
              :gwt_dev

    package(:jar)
    package(:sources)
    package(:javadoc)

    package(:jar).enhance do |jar|
      jar.merge(artifact(:javapoet))
      jar.merge(artifact(:proton_core))
      jar.enhance do |f|
        shaded_jar = (f.to_s + '-shaded')
        Buildr.ant 'shade_jar' do |ant|
          artifact = Buildr.artifact(:shade_task)
          artifact.invoke
          ant.taskdef :name => 'shade', :classname => 'org.realityforge.ant.shade.Shade', :classpath => artifact.to_s
          ant.shade :jar => f.to_s, :uberJar => shaded_jar do
            ant.relocation :pattern => 'com.squareup.javapoet', :shadedPattern => 'react4j.processor.vendor.javapoet'
            ant.relocation :pattern => 'org.realityforge.proton', :shadedPattern => 'react4j.processor.vendor.proton'
          end
        end
        FileUtils.mv shaded_jar, f.to_s
      end
    end

    test.using :testng
    test.options[:properties] = { 'react4j.fixture_dir' => _('src/test/fixtures') }
    test.options[:java_args] = ['-ea']

    iml.test_source_directories << _('src/test/fixtures/input')
    iml.test_source_directories << _('src/test/fixtures/expected')
    iml.test_source_directories << _('src/test/fixtures/bad_input')
  end

  desc 'Test React4j in downstream projects'
  define 'downstream-test' do
    compile.with :gir,
                 :javax_annotation

    test.options[:properties] =
      {
        'react4j.next.core.jar' => project('core').package(:jar).to_s,
        'react4j.next.dom.jar' => project('dom').package(:jar).to_s,
        'react4j.current.version' => ENV['PREVIOUS_PRODUCT_VERSION'] || project.version,
        'react4j.next.version' => ENV['PRODUCT_VERSION'] || project.version,
        'react4j.build_j2cl_variants' => (ENV['J2CL'] != 'no'),
        'react4j.deploy_test.fixture_dir' => _('src/test/resources/fixtures').to_s,
        'react4j.deploy_test.work_dir' => _(:target, 'deploy_test/workdir').to_s
      }
    test.options[:java_args] = ['-ea']

    local_test_repository_url = URI.join('file:///', project._(:target, :local_test_repository)).to_s
    compile.enhance do
      projects_to_upload = projects(%w(core processor dom))
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
            prj.packages.each(&:upload)
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
      properties['react4j.build_j2cl_variants'] = ENV['J2CL'] != 'no'
      properties['react4j.deploy_test.work_dir'] = _(:target, 'deploy_test/workdir').to_s
      properties['react4j.deploy_test.fixture_dir'] = _('src/test/resources/fixtures').to_s
      properties['react4j.deploy_test.local_repository_url'] = local_test_repository_url
      properties['react4j.deploy_test.store_statistics'] = ENV['STORE_BUILD_STATISTICS'] == 'true'
      properties['react4j.deploy_test.build_before'] = (ENV['STORE_BUILD_STATISTICS'] != 'true' && ENV['BUILD_BEFORE'] != 'no')

      Java::Commands.java 'react4j.downstream.CollectWebSpeechDemoBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
      Java::Commands.java 'react4j.downstream.CollectDrumLoopBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
      Java::Commands.java 'react4j.downstream.CollectFluxChallengeBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
      Java::Commands.java 'react4j.downstream.BuildDownstream', { :classpath => cp, :properties => properties } unless ENV['DOWNSTREAM'] == 'no'
      Java::Commands.java 'react4j.downstream.CollectBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
    end

    # Only run this test when preparing for release, never on TravisCI (as produces different byte sizes)
    test.exclude '*BuildStatsTest' if ENV['PRODUCT_VERSION'].nil? || ENV['BUILD_STATS'] == 'no' || !ENV['TRAVIS_BUILD_NUMBER'].nil?
    test.exclude '*BuildOutputTest' if ENV['BUILD_STATS'] == 'no'

    test.using :testng
    test.compile.with :javax_annotation,
                      :javacsv,
                      :grim_asserts,
                      :javax_json,
                      :gwt_symbolmap,
                      :jetbrains_annotations,
                      :testng
  end

  desc 'Examples that are only used to illustrate ideas in documentation'
  define 'doc-examples' do
    project.enable_annotation_processor = true

    compile.with project('dom').package(:jar),
                 project('dom').compile.dependencies,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies,
                 DAGGER_PROCESSOR_DEPS,
                 :arez_processor,
                 :sting_core,
                 :sting_processor,
                 :gwt_user

    gwt_enhance(project, :modules_complete => true, :package_jars => false, :output_key => 'react4j-doc-examples')
  end

  doc.from(projects(%w(core dom processor))).
    using(:javadoc,
          :windowtitle => 'React4j API Documentation',
          :linksource => true,
          :timestamp => false,
          :link => %w(https://arez.github.io/api https://arez.github.io/spytools https://docs.oracle.com/javase/8/docs/api http://www.gwtproject.org/javadoc/latest/),
          :group => {
            'Core Packages' => 'react4j*',
            'DOM Packages' => 'react4j.dom*',
            'Annotation Packages' => 'react4j.annotations*:react4j.processor*'
          }
    ).sourcepath = project('core').compile.sources + project('dom').compile.sources + project('processor').compile.sources

  iml.excluded_directories << project._('tmp')
  iml.excluded_directories << project._('node_modules')

  ipr.add_component_from_artifact(:idea_codestyle)

  ipr.add_component('JavacSettings') do |xml|
    xml.option(:name => 'ADDITIONAL_OPTIONS_STRING', :value => '-Xlint:all,-processing,-serial -Werror -Xmaxerrs 10000 -Xmaxwarns 10000')
  end

  EXAMPLES.each_pair do |key, gwt_module|
    ipr.add_gwt_configuration(project('doc-examples'),
                              :name => "GWT: #{key}",
                              :gwt_module => gwt_module,
                              :start_javascript_debugger => false,
                              :vm_parameters => '-Xmx2G',
                              :shell_parameters => "-strict -style PRETTY -XmethodNameDisplayMode FULL -nostartServer -incremental -codeServerPort 8889 -bindAddress 0.0.0.0 -deploy #{_(:generated, :gwt, 'deploy')} -extra #{_(:generated, :gwt, 'extra')} -war #{_(:generated, :gwt, 'war')}",
                              :launch_page => "http://127.0.0.1:8889/#{gwt_module}/index.html")
  end

  ipr.add_default_testng_configuration(:jvm_args => "-ea -Dbraincheck.environment=development -Darez.environment=development -Dreact4j.environment=development -Dreact4j.output_fixture_data=false -Dreact4j.fixture_dir=processor/src/test/fixtures -Dreact4j.current.version=X -Dreact4j.next.version=X -Dreact4j.deploy_test.work_dir=#{project('downstream-test')._(:target, 'deploy_test/workdir')} -Dreact4j.deploy_test.fixture_dir=#{project('downstream-test')._('src/test/resources/fixtures')} -Dreact4j.deploy_test.local_repository_url=#{URI.join('file:///', project('downstream-test')._(:target, :local_test_repository))} -Dreact4j.deploy_test.store_statistics=false -Dreact4j.core.compile_target=target/react4j_core/idea/classes -Dreact4j.dom.compile_target=target/react4j_dom/idea/classes")

  ipr.add_testng_configuration('core',
                               :module => 'core',
                               :jvm_args => '-ea -Dbraincheck.environment=development -Darez.environment=development -Dreact4j.environment=development -Dreact4j.core.compile_target=../target/react4j_core/idea/classes')
  ipr.add_testng_configuration('dom',
                               :module => 'dom',
                               :jvm_args => '-ea -Dbraincheck.environment=development -Darez.environment=development -Dreact4j.environment=development -Dreact4j.dom.compile_target=../target/react4j_dom/idea/classes')
  ipr.add_testng_configuration('processor',
                               :module => 'processor',
                               :jvm_args => '-ea -Dreact4j.output_fixture_data=true -Dreact4j.fixture_dir=src/test/fixtures')

  ipr.nonnull_assertions = false

  ipr.add_component('JavaProjectCodeInsightSettings') do |xml|
    xml.tag!('excluded-names') do
      xml << '<name>com.sun.istack.internal.NotNull</name>'
      xml << '<name>com.sun.istack.internal.Nullable</name>'
      xml << '<name>org.jetbrains.annotations.Nullable</name>'
      xml << '<name>org.jetbrains.annotations.NotNull</name>'
      xml << '<name>org.testng.AssertJUnit</name>'
    end
  end
  ipr.add_component('NullableNotNullManager') do |component|
    component.option :name => 'myDefaultNullable', :value => 'javax.annotation.Nullable'
    component.option :name => 'myDefaultNotNull', :value => 'javax.annotation.Nonnull'
    component.option :name => 'myNullables' do |option|
      option.value do |value|
        value.list :size => '2' do |list|
          list.item :index => '0', :class => 'java.lang.String', :itemvalue => 'org.jetbrains.annotations.Nullable'
          list.item :index => '1', :class => 'java.lang.String', :itemvalue => 'javax.annotation.Nullable'
        end
      end
    end
    component.option :name => 'myNotNulls' do |option|
      option.value do |value|
        value.list :size => '2' do |list|
          list.item :index => '0', :class => 'java.lang.String', :itemvalue => 'org.jetbrains.annotations.NotNull'
          list.item :index => '1', :class => 'java.lang.String', :itemvalue => 'javax.annotation.Nonnull'
        end
      end
    end
  end
end

Buildr.projects.each do |project|
  project.jacoco.enabled = false unless project.name == 'react4j:processor'
end
