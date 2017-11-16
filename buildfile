require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'

PROVIDED_DEPS = [:javax_jsr305, :jetbrains_annotations, :anodoc]
TEST_DEPS = []

# JDK options passed to test environment. Essentially turns assertions on.
REACT_TEST_OPTIONS =
  {
    'braincheck.environment' => 'development',
    'react4j.environment' => 'development'
  }

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

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  desc 'React4j DOM binding'
  define 'dom' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    js_assets(project, :dom)

    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :gwt_user, # for the javaemul.internal.annotations annotations
                 :elemental2_dom,
                 :elemental2_promise

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    generate_factory_source(project)
    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  desc 'React4j-Arez Integration'
  define 'arez' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :elemental2_dom,
                 :elemental2_promise,
                 :arez_core,
                 :arez_annotations,
                 :arez_extras,
                 :arez_browser_extras

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  desc 'Interoperability with GWT Widget API'
  define 'widget' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with project('dom').package(:jar),
                 project('dom').compile.dependencies,
                 :gwt_user

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with TEST_DEPS
  end

  desc 'The Annotation processor'
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
    test.options[:properties] = { 'react4j.fixture_dir' => _('src/test/resources') }
    test.compile.with TEST_DEPS

    # The generators are configured to generate to here.
    iml.test_source_directories << _('generated/processors/test/java')

    iml.test_source_directories << _('src/test/resources/input')
    iml.test_source_directories << _('src/test/resources/expected')
    iml.test_source_directories << _('src/test/resources/bad_input')
  end

  desc 'Examples that are only used to illustrate ideas in documentation'
  define 'doc-examples' do
    pom.provided_dependencies.concat PROVIDED_DEPS

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
                 :arez_processor,
                 :arez_component,
                 :arez_extras,
                 :arez_browser_extras,
                 :gwt_user

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project, :modules_complete => true, :package_jars => false, :output_key => 'react4j-doc-examples')

    test.using :testng
    test.compile.with TEST_DEPS

    # The generators are configured to generate to here.
    iml.main_generated_source_directories << _('generated/processors/main/java')
  end

  # These will one day move to a separate repository once the API stabilizes
  desc 'Other assorted components'
  define 'extras' do
    pom.provided_dependencies.concat PROVIDED_DEPS

    compile.with project('dom').package(:jar),
                 project('dom').compile.dependencies,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    package(:jar)
    package(:sources)
    package(:javadoc)

    gwt_enhance(project)

    test.using :testng
    test.compile.with TEST_DEPS

    # The generators are configured to generate to here.
    iml.main_generated_source_directories << _('generated/processors/main/java')
  end

  # This should be moved to a separate project once the APIs have stabilized.
  desc 'Sample TodoMVC implementation used during development'
  define 'todomvc' do
    pom.provided_dependencies.concat PROVIDED_DEPS

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
                 :arez_processor,
                 :arez_component,
                 :arez_extras,
                 :arez_browser_extras,
                 :gwt_user

    test.options[:properties] = REACT_TEST_OPTIONS
    test.options[:java_args] = ['-ea']

    gwt_enhance(project, :modules_complete => true, :package_jars => false)

    test.using :testng
    test.compile.with TEST_DEPS

    # The generators are configured to generate to here.
    iml.main_generated_source_directories << _('generated/processors/main/java')
  end

  doc.from(projects(%w(annotations core dom arez processor widget))).
    using(:javadoc,
          :windowtitle => 'React4j API Documentation',
          :linksource => true,
          :link => %w(https://arez.github.io/arez/api https://docs.oracle.com/javase/8/docs/api http://www.gwtproject.org/javadoc/latest/),
          :group => {
            'Core Packages' => 'react4j.core*',
            'DOM Packages' => 'react4j.dom*',
            'Annotation Packages' => 'react4j.annotations*:react4j.processor*',
            'Arez Packages' => 'react4j.arez*',
            'GWT Widget Integration Packages' => 'react4j.widget*'
          }
    )

  iml.excluded_directories << project._('tmp')
  iml.excluded_directories << project._('node_modules')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development -Dreact4j.environment=development -Dreact4j.output_fixture_data=false -Dreact4j.fixture_dir=processor/src/test/resources')
  ipr.add_component_from_artifact(:idea_codestyle)

  EXAMPLES.each_pair do |key, gwt_module|
    ipr.add_gwt_configuration(project('doc-examples'),
                              :name => "GWT: #{key}",
                              :gwt_module => gwt_module,
                              :start_javascript_debugger => false,
                              :vm_parameters => "-Xmx2G -Djava.io.tmpdir=#{_('tmp/gwt')}",
                              :shell_parameters => "-port 8888 -codeServerPort 8889 -bindAddress 0.0.0.0 -war #{_(:generated, 'gwt-export')}/")
  end

  ipr.add_gwt_configuration(project('todomvc'),
                            :gwt_module => 'react4j.todomvc.TodomvcDev',
                            :start_javascript_debugger => false,
                            :vm_parameters => "-Xmx2G -Djava.io.tmpdir=#{_('tmp/gwt')}",
                            :shell_parameters => "-port 8888 -codeServerPort 8889 -bindAddress 0.0.0.0 -war #{_(:generated, 'gwt-export')}/")

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

Buildr.projects.each do |project|
  project.clean do
    rm_rf project._(:generated)
  end
end
