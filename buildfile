require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/gwt'

GWT_DEPS =
  [
    :gwt_user,
    :gwt_interop_utils,
    :react,
    :react_router,
    :elemental2_core,
    :elemental2_dom,
    :elemental2_promise,
    :jsinterop_base,
    :jsinterop_base_sources,
    :jsinterop_annotations,
    :jsinterop_annotations_sources
  ]
PROVIDED_DEPS = [:javax_jsr305, :jetbrains_annotations]
COMPILE_DEPS = [] + GWT_DEPS
OPTIONAL_DEPS = []
TEST_DEPS = []

desc 'GwtReactPlayground: Experimentation app'
define 'playground' do
  project.group = 'org.realityforge.gwt.react.playground'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  compile.with PROVIDED_DEPS, COMPILE_DEPS

  test.options[:java_args] = ['-ea']

  package(:jar)
  package(:sources)
  package(:javadoc)

  test.using :testng
  test.compile.with TEST_DEPS

  iml.add_gwt_facet({ 'gwt.react.todo_mvc.todomvc' => true },
                    :settings =>
                      {
                        :compilerParameters => '-generateJsInteropExports -draftCompile -localWorkers 2 -strict',
                        :compilerMaxHeapSize => '4096'
                      },
                    :gwt_dev_artifact => :gwt_dev)

  iml.excluded_directories << project._('tmp/gwt')

  ipr.add_default_testng_configuration(:jvm_args => '-ea')
  ipr.add_component_from_artifact(:idea_codestyle)

  ipr.add_gwt_configuration(project,
                            :gwt_module => 'gwt.react.todo_mvc.todomvc',
                            :start_javascript_debugger => false,
                            :vm_parameters => "-Xmx3G -Djava.io.tmpdir=#{_('tmp/gwt')}",
                            :shell_parameters => "-generateJsInteropExports -port 8888 -codeServerPort 8889 -bindAddress 0.0.0.0 -war #{_(:generated, 'gwt-export')}/")
end
