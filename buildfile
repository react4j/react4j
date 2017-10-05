require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'

PROVIDED_DEPS = [:javax_jsr305, :jetbrains_annotations]
TEST_DEPS = []

# JDK options passed to test environment. Essentially turns assertions on.
REACT_TEST_OPTIONS =
  {
    'braincheck.dynamic_provider' => 'true',
    'braincheck.environment' => 'development',
    'react.dynamic_provider' => 'true',
    'react.logger' => 'proxy',
    'react.environment' => 'development'
  }

EXAMPLES = {
  'hello_world' => 'react4j.examples.hello_world.HelloWorld'
}

desc 'React4j: An opinionated Java binding for React'
define 'react4j' do
  project.group = 'org.realityforge.react'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  desc 'Annotations for defining a react component'
  define 'annotations' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with PROVIDED_DEPS,
                 :jsinterop_base,
                 :jsinterop_base_sources

    gwt_enhance(project, ['react4j.annotations.Annotations'])

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'The core react binding'
  define 'core' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    js_assets(project, :core)

    compile.with PROVIDED_DEPS,
                 :elemental2_core,
                 :jsinterop_base,
                 :jsinterop_base_sources,
                 :jsinterop_annotations,
                 :jsinterop_annotations_sources,
                 :braincheck

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project, %w(react4j.core.React react4j.core.ReactDev))

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  define 'dom' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    js_assets(project, :dom)

    compile.with project('core').package(:jar, :classifier => :gwt),
                 project('core').compile.dependencies,
                 :elemental2_dom,
                 :elemental2_promise

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    generate_factory_source(project)
    gwt_enhance(project, %w(react4j.dom.ReactDOM))

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  define 'arez' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with project('core').package(:jar, :classifier => :gwt),
                 project('core').compile.dependencies,
                 :elemental2_core,
                 :elemental2_dom,
                 :elemental2_promise,
                 :arez_core,
                 :arez_annotations,
                 :arez_extras,
                 :arez_browser_extras

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project, %w(react4j.arez.ReactArez))

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  define 'processor' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with :autoservice,
                 :autocommon,
                 :javapoet,
                 :guava,
                 project('annotations').package(:jar),
                 project('annotations').compile.dependencies,
                 project('core').package(:jar),
                 project('core').compile.dependencies

    test.with :compile_testing,
              Java.tools_jar,
              :truth,
              project('arez').package(:jar),
              project('arez').compile.dependencies,
              :arez_processor,
              :gwt_user,
              :gwt_dev

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS

    # The generators are configured to generate to here.
    iml.test_source_directories << _('generated/processors/test/java')

    iml.test_source_directories << _('src/test/resources/input')
    iml.test_source_directories << _('src/test/resources/expected')
    iml.test_source_directories << _('src/test/resources/bad_input')
  end

  define 'examples' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with project('annotations').package(:jar, :classifier => :gwt),
                 project('annotations').compile.dependencies,
                 project('core').package(:jar, :classifier => :gwt),
                 project('core').compile.dependencies,
                 project('dom').package(:jar, :classifier => :gwt),
                 project('dom').compile.dependencies,
                 project('arez').package(:jar, :classifier => :gwt),
                 project('arez').compile.dependencies,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies,
                 :arez_processor,
                 :arez_extras,
                 :arez_browser_extras,
                 :gwt_user

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project, EXAMPLES.values, :modules_complete => true)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS

    # The generators are configured to generate to here.
    iml.main_source_directories << _('generated/processors/main/java')
    no_uploads!
  end

  define 'todomvc' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with project('annotations').package(:jar, :classifier => :gwt),
                 project('annotations').compile.dependencies,
                 project('core').package(:jar, :classifier => :gwt),
                 project('core').compile.dependencies,
                 project('dom').package(:jar, :classifier => :gwt),
                 project('dom').compile.dependencies,
                 project('arez').package(:jar, :classifier => :gwt),
                 project('arez').compile.dependencies,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies,
                 :arez_processor,
                 :arez_extras,
                 :arez_browser_extras,
                 :gwt_user

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project, %w(react4j.todomvc.Todomvc))

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS

    # The generators are configured to generate to here.
    iml.main_source_directories << _('generated/processors/main/java')
    no_uploads!
  end

  doc.from(projects(%w(annotations core dom arez processor))).using(:javadoc, :windowtitle => 'React')

  iml.excluded_directories << project._('tmp/gwt')
  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.dynamic_provider=true -Dbraincheck.environment=development -Dreact.dynamic_provider=true -Dreact.environment=development -Dreact.output_fixture_data=false -Dreact.fixture_dir=processor/src/test/resources')
  ipr.add_component_from_artifact(:idea_codestyle)

  EXAMPLES.each_pair do |key, gwt_module|
    ipr.add_gwt_configuration(project('examples'),
                              :name => "GWT: #{key}",
                              :gwt_module => gwt_module,
                              :start_javascript_debugger => false,
                              :vm_parameters => "-Xmx2G -Djava.io.tmpdir=#{_('tmp/gwt')}",
                              :shell_parameters => "-generateJsInteropExports -port 8888 -codeServerPort 8889 -bindAddress 0.0.0.0 -war #{_(:generated, 'gwt-export')}/")
  end

  ipr.add_gwt_configuration(project('todomvc'),
                            :gwt_module => 'org.realityforge.react.todo_mvc.todomvc',
                            :start_javascript_debugger => false,
                            :vm_parameters => "-Xmx2G -Djava.io.tmpdir=#{_('tmp/gwt')}",
                            :shell_parameters => "-generateJsInteropExports -port 8888 -codeServerPort 8889 -bindAddress 0.0.0.0 -war #{_(:generated, 'gwt-export')}/")

  ipr.add_component('CompilerConfiguration') do |component|
    component.annotationProcessing do |xml|
      xml.profile(:default => true, :name => 'Default', :enabled => true) do
        xml.sourceOutputDir :name => 'generated/processors/main/java'
        xml.sourceTestOutputDir :name => 'generated/processors/test/java'
        xml.outputRelativeToContentRoot :value => true
      end
    end
  end
end
