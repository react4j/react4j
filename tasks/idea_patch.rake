expected_versions = %w(1.5.3 1.5.4)
unless expected_versions.include?(Buildr::VERSION.to_s)
  raise "Patch should no longer be required unless Buildr versions are #{expected_versions.join(', ')} but actual version is #{Buildr::VERSION}"
end

class Buildr::IntellijIdea::IdeaProject

  def add_gwt_configuration(project, options = {})
    launch_page = options[:launch_page]
    name = options[:name] || (launch_page ? "Run #{launch_page}" : "Run #{project.name} DevMode")
    shell_parameters = options[:shell_parameters]
    vm_parameters = options[:vm_parameters] || '-Xmx512m'
    singleton = options[:singleton].nil? ? true : !!options[:singleton]
    super_dev = options[:super_dev].nil? ? true : !!options[:super_dev]
    gwt_module = options[:gwt_module]

    start_javascript_debugger = options[:start_javascript_debugger].nil? ? true : !!options[:start_javascript_debugger]

    add_configuration(name, 'GWT.ConfigurationType', 'GWT Configuration', false, :singleton => singleton) do |xml|
      xml.module(:name => options[:iml_name] || project.iml.name)

      xml.option(:name => 'VM_PARAMETERS', :value => vm_parameters)
      xml.option(:name => 'RUN_PAGE', :value => launch_page) if launch_page
      xml.option(:name => 'GWT_MODULE', :value => gwt_module) if gwt_module

      # noinspection RubySimplifyBooleanInspection
      xml.option(:name => 'OPEN_IN_BROWSER', :value => false) if options[:open_in_browser] == false
      xml.option(:name => 'START_JAVASCRIPT_DEBUGGER', :value => start_javascript_debugger)
      xml.option(:name => 'USE_SUPER_DEV_MODE', :value => super_dev)
      xml.option(:name => 'SHELL_PARAMETERS', :value => shell_parameters) if shell_parameters

      xml.RunnerSettings(:RunnerId => 'Debug') do
        xml.option(:name => 'DEBUG_PORT', :value => '')
        xml.option(:name => 'TRANSPORT', :value => 0)
        xml.option(:name => 'LOCAL', :value => true)
      end

      xml.RunnerSettings(:RunnerId => 'Run')
      xml.ConfigurationWrapper(:RunnerId => 'Run')
      xml.ConfigurationWrapper(:RunnerId => 'Debug')
      xml.method
    end
  end
end
