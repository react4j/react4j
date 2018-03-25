module Buildr
  module ProcessorPath
    module ProjectExtension
      include Extension

      attr_writer :enable_annotation_processor

      def enable_annotation_processor?
        @enable_annotation_processor.nil? ? !self.processorpath.empty? : !!@enable_annotation_processor
      end

      def processorpath
        @processorpath ||= []
      end

      before_define do |project|
        t = project.task('processors_setup') do
          mkdir_p project._(:generated, 'processors/main/java') if project.enable_annotation_processor?
        end
        project.compile.enhance([t.name])

        if project.iml?
          project.iml.instance_variable_set('@main_generated_source_directories', [])
          project.iml.instance_variable_set('@processorpath', {})
        end
      end

      after_define do |project|
        if project.enable_annotation_processor?
          project.file(project._(:generated, 'processors/main/java')).enhance([project.compile])

          project.compile.options.merge!(:other => ['-s', project._(:generated, 'processors/main/java')])
          if project.iml? && project.enable_annotation_processor?
            project.iml.main_generated_source_directories << project._(:generated, 'processors/main/java')
          end
          project.clean do
            # Clean the IDE generated sources
            rm_rf project._(:generated, 'processors/main/java')
          end
        end

        unless project.processorpath.empty?
          processor_deps = Buildr.artifacts(project.processorpath)
          project.compile.enhance(processor_deps)
          pp = processor_deps.collect {|d| d.to_s}.join(File::PATH_SEPARATOR)
          project.compile.options[:other] += ['-processorpath', pp]
        end

        if project.ipr?
          project.ipr.add_component('CompilerConfiguration') do |component|
            component.annotationProcessing do |xml|
              xml.profile(:default => true, :name => 'Default', :enabled => true) do
                xml.sourceOutputDir :name => 'generated/processors/main/java'
                xml.sourceTestOutputDir :name => 'generated/processors/test/java'
                xml.outputRelativeToContentRoot :value => true
                xml.processorPath :useClasspath => true
              end
              enabled = Buildr.projects(:no_invoke => true).select {|p| p.iml? && p.enable_annotation_processor?}
              enabled.each do |prj|
                xml.profile(:name => "#{prj.name}", :enabled => true) do
                  xml.sourceOutputDir :name => 'generated/processors/main/java'
                  xml.sourceTestOutputDir :name => 'generated/processors/test/java'
                  xml.outputRelativeToContentRoot :value => true
                  xml.module :name => prj.iml.name
                  if prj.processorpath.empty?
                    xml.processorPath :useClasspath => true
                  else
                    xml.processorPath :useClasspath => false do
                      Buildr.artifacts(prj.processorpath).each do |path|
                        xml.entry :name => project.ipr.send(:resolve_path, path.to_s)
                      end
                    end
                  end
                end
              end
              disabled = Buildr.projects(:no_invoke => true).select {|p| p.iml? && !p.enable_annotation_processor?}
              unless disabled.empty?
                xml.profile(:name => 'Disabled') do
                  disabled.each do |p|
                    xml.module :name => p.name
                  end
                end
              end
            end
          end
        end
      end
    end
  end
end

class Buildr::Project
  include Buildr::ProcessorPath::ProjectExtension
end
