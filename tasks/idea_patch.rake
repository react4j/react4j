raise "Patch applied upstream " if Buildr::VERSION.to_s > '1.5.8'

class Buildr::IntellijIdea::IdeaProject
  def add_configuration(name, type, factory_name = nil, default = false, options = {})
    add_to_composite_component(self.configurations) do |xml|
      params = options.dup
      params[:type] = type
      params[:factoryName] = factory_name if factory_name
      params[:name] = name unless default
      params[:default] = !!default
      xml.configuration(params) do
        yield xml if block_given?
      end
    end
  end

  def add_testng_configuration(name, options = {})
    jvm_args = options[:jvm_args] || '-ea'
    module_name = options[:module] || ''
    dir = options[:dir]

    opts = {}
    opts[:folderName] = options[:folderName] if options[:folderName]
    add_configuration(name, 'TestNG', nil, false, opts) do |xml|
      xml.module(:name => module_name)
      xml.option(:name => 'SUITE_NAME', :value => '')
      xml.option(:name => 'PACKAGE_NAME', :value => '')
      xml.option(:name => 'MAIN_CLASS_NAME', :value => '')
      xml.option(:name => 'METHOD_NAME', :value => '')
      xml.option(:name => 'GROUP_NAME', :value => '')
      xml.option(:name => 'TEST_OBJECT', :value => 'PACKAGE')
      xml.option(:name => 'VM_PARAMETERS', :value => jvm_args)
      xml.option(:name => 'PARAMETERS', :value => '-configfailurepolicy continue')
      xml.option(:name => 'WORKING_DIRECTORY', :value => dir) if dir
      xml.option(:name => 'OUTPUT_DIRECTORY', :value => '')
      xml.option(:name => 'PROPERTIES_FILE', :value => '')
      xml.properties
      xml.listeners
      xml.method(:v => '2') do
        xml.option(:name => 'Make', :enabled => 'true')
      end
    end
  end
end
if Buildr::VERSION.to_s == '1.5.8'
  class Buildr::IntellijIdea::IdeaModule

    protected

    def main_dependency_details
      target_dir = buildr_project.compile.target.to_s
      main_dependencies.select {|d| d.to_s != target_dir}.collect do |d|
        dependency_path = d.to_s
        export = true
        source_path = nil
        annotations_path = nil
        if d.is_a?(Buildr::Artifact)
          source_spec = d.to_spec_hash.merge(:classifier => 'sources')
          source_path = Buildr.artifact(source_spec).to_s
          source_path = nil unless File.exist?(source_path)
        end
        if d.is_a?(Buildr::Artifact)
          annotations_spec = d.to_spec_hash.merge(:classifier => 'annotations')
          annotations_path = Buildr.artifact(annotations_spec).to_s
          annotations_path = nil unless File.exist?(annotations_path)
        end
        [dependency_path, export, source_path, annotations_path]
      end
    end

    def test_dependency_details
      main_dependencies_paths = main_dependencies.map(&:to_s)
      target_dir = buildr_project.compile.target.to_s
      test_dependencies.select {|d| d.to_s != target_dir}.collect do |d|
        dependency_path = d.to_s
        export = main_dependencies_paths.include?(dependency_path)
        source_path = nil
        annotations_path = nil
        if d.is_a?(Buildr::Artifact)
          source_spec = d.to_spec_hash.merge(:classifier => 'sources')
          source_path = Buildr.artifact(source_spec).to_s
          source_path = nil unless File.exist?(source_path)
        end
        if d.is_a?(Buildr::Artifact)
          annotations_spec = d.to_spec_hash.merge(:classifier => 'annotations')
          annotations_path = Buildr.artifact(annotations_spec).to_s
          annotations_path = nil unless File.exist?(annotations_path)
        end
        [dependency_path, export, source_path, annotations_path]
      end
    end
  end
end
