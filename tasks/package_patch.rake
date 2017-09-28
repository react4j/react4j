# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with this
# work for additional information regarding copyright ownership.  The ASF
# licenses this file to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.

expected_versions = %w(1.5.3)
unless expected_versions.include?(Buildr::VERSION.to_s)
  raise "Patch should no longer be required unless Buildr versions are #{expected_versions.join(', ')} but actual version is #{Buildr::VERSION}"
end

module Buildr
  module Package
    def package(*args)
      spec = Hash === args.last ? args.pop.dup : {}
      no_options = spec.empty? # since spec is mutated
      if spec[:file]
        rake_check_options spec, :file, :type
        spec[:type] = args.shift || spec[:type] || spec[:file].split('.').last.to_sym
        file_name = spec[:file]
      else
        rake_check_options spec, *ActsAsArtifact::ARTIFACT_ATTRIBUTES
        spec[:id] ||= self.id
        spec[:group] ||= self.group
        spec[:version] ||= self.version
        spec[:type] = args.shift || spec[:type] || compile.packaging || :zip
      end

      packager = method("package_as_#{spec[:type]}") rescue fail("Don't know how to create a package of type #{spec[:type]}")
      if packager.arity == 1
        unless file_name
          spec = send("package_as_#{spec[:type]}_spec", spec) if respond_to?("package_as_#{spec[:type]}_spec")
          file_name = path_to(:target, Artifact.hash_to_file_name(spec))
        end
        package = (no_options && packages.detect { |pkg| pkg.type == spec[:type] && (pkg.id.nil? || pkg.id == spec[:id]) &&
          (pkg.respond_to?(:classifier) ? pkg.classifier : nil) == spec[:classifier]}) ||
          packages.find { |pkg| pkg.name == file_name } ||
          packager.call(file_name)
      else
        Buildr.application.deprecated "We changed the way package_as methods are implemented.  See the package method documentation for more details."
        file_name ||= path_to(:target, Artifact.hash_to_file_name(spec))
        package = packager.call(file_name, spec)
      end

      # First time: prepare package for install, uninstall and upload tasks.
      unless packages.include?(package)
        # We already run build before package, but we also need to do so if the package itself is
        # used as a dependency, before we get to run the package task.
        task 'package'=>package
        package.enhance [task('build')]
        package.enhance { info "Packaging #{File.basename(file_name)}" }
        if spec[:file]
          class << package ; self ; end.send(:define_method, :type) { spec[:type] }
          class << package ; self ; end.send(:define_method, :id) { nil }
        else
          # Make it an artifact using the specifications, and tell it how to create a POM.
          package.extend ActsAsArtifact
          package.buildr_project = self
          package.send :apply_spec, spec.only(*Artifact::ARTIFACT_ATTRIBUTES)

          # Create pom associated with package
          class << package
            def pom
              unless @pom
                pom_filename = Util.replace_extension(self.name, 'pom')
                spec = {:group=>group, :id=>id, :version=>version, :type=>:pom}
                @pom = Buildr.artifact(spec, pom_filename)
                @pom.content Buildr::CustomPom.pom_xml(self.buildr_project, self)
              end
              @pom
            end
          end if package.classifier.nil?

          file(Buildr.repositories.locate(package)=>package) { package.install }

          # Add the package to the list of packages created by this project, and
          # register it as an artifact. The later is required so if we look up the spec
          # we find the package in the project's target directory, instead of finding it
          # in the local repository and attempting to install it.
          Artifact.register package
          Artifact.register package.pom if package.classifier.nil?
        end

        task('install')   { package.install if package.respond_to?(:install) }
        task('uninstall') { package.uninstall if package.respond_to?(:uninstall) }
        task('upload')    { package.upload if package.respond_to?(:upload) }

        packages << package
      end
      package
    end
  end
end

class Buildr::Project
  include Buildr::Package
end
