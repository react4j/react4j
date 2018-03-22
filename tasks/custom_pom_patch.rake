raise 'Patch already integrated into buildr code' unless Buildr::VERSION.to_s == '1.5.5'

class Buildr::CustomPom
  attr_accessor :dependency_filter

  def additional_dependencies
    @additional_dependencies ||= []
  end

  def additional_dependencies=(additional_dependencies)
    @additional_dependencies = additional_dependencies
  end

  def include_transitive_dependencies
    @include_transitive_dependencies ||= []
  end

  def include_transitive_dependencies=(include_transitive_dependencies)
    @include_transitive_dependencies = include_transitive_dependencies
  end

  def self.pom_xml(project, package)
    Proc.new do
      xml = Builder::XmlMarkup.new(:indent => 2)
      xml.instruct!
      xml.project('xmlns' => 'http://maven.apache.org/POM/4.0.0',
                  'xmlns:xsi' => 'http://www.w3.org/2001/XMLSchema-instance',
                  'xsi:schemaLocation' => 'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd') do
        xml.modelVersion '4.0.0'
        xml.parent do
          xml.groupId 'org.sonatype.oss'
          xml.artifactId 'oss-parent'
          # PD - patch parent POM version
          xml.version '8'
        end
        xml.groupId project.group
        xml.artifactId project.id
        xml.version project.version
        candidates = project.packages.select {|p| p.classifier.nil?}.collect {|p| p.type.to_s}
        packaging = !candidates.empty? ? candidates[0] : (project.compile.packaging || :zip).to_s
        xml.packaging packaging

        xml.name project.pom.name if project.pom.name
        xml.description project.pom.description if project.pom.description
        xml.url project.pom.url if project.pom.url

        xml.licenses do
          project.pom.licenses.each_pair do |name, url|
            xml.license do
              xml.name name
              xml.url url
              xml.distribution 'repo'
            end
          end
        end unless project.pom.licenses.empty?

        if project.pom.scm_url || project.pom.scm_connection || project.pom.scm_developer_connection
          xml.scm do
            xml.connection project.pom.scm_connection if project.pom.scm_connection
            xml.developerConnection project.pom.scm_developer_connection if project.pom.scm_developer_connection
            xml.url project.pom.scm_url if project.pom.scm_url
          end
        end

        if project.pom.issues_url
          xml.issueManagement do
            xml.url project.pom.issues_url
            xml.system project.pom.issues_system if project.pom.issues_system
          end
        end

        xml.developers do
          project.pom.developers.each do |developer|
            xml.developer do
              xml.id developer.id
              xml.name developer.name if developer.name
              xml.email developer.email if developer.email
              if developer.roles
                xml.roles do
                  developer.roles.each do |role|
                    xml.role role
                  end
                end
              end
            end
          end
        end unless project.pom.developers.empty?

        provided_deps = Buildr.artifacts(project.pom.provided_dependencies)
        runtime_deps = Buildr.artifacts(project.pom.runtime_dependencies)
        additional_deps = Buildr.artifacts(project.pom.additional_dependencies)
        include_transitive_deps = Buildr.artifacts(project.pom.include_transitive_dependencies).collect {|dep| dep.to_s}
        optional_deps = Buildr.artifacts(project.pom.optional_dependencies).collect{|dep| dep.to_s}

        done = []

        deps = []
        deps += provided_deps.
          select {|d| d.is_a?(ActsAsArtifact)}.
          select {|d| !done.include?(d.to_s)}.
          collect {|dep| done << dep.to_s; dep.to_hash.merge(:scope => 'provided', :optional => optional_deps.include?(dep.to_s), :include_transitive => include_transitive_deps.include?(dep.to_s), :artifact => dep)}
        deps += runtime_deps.
          select {|d| d.is_a?(ActsAsArtifact)}.
          select {|d| !done.include?(d.to_s)}.
          collect {|dep| done << dep.to_s; dep.to_hash.merge(:scope => 'runtime', :optional => optional_deps.include?(dep.to_s), :include_transitive => include_transitive_deps.include?(dep.to_s), :artifact => dep)}
        deps += additional_deps.
          select {|d| d.is_a?(ActsAsArtifact)}.
          select {|d| !done.include?(d.to_s)}.
          collect {|dep| done << dep.to_s; dep.to_hash.merge(:scope => 'compile', :optional => optional_deps.include?(dep.to_s), :include_transitive => include_transitive_deps.include?(dep.to_s), :artifact => dep)}

        deps +=
          Buildr.artifacts(project.compile.dependencies).
            select {|d| d.is_a?(ActsAsArtifact)}.
            select {|d| !done.include?(d.to_s)}.
            collect {|d| done << d.to_s; d.to_hash.merge(:scope => 'compile', :optional => optional_deps.include?(d.to_s), :include_transitive => include_transitive_deps.include?(d.to_s), :artifact => d)}

        deps += Buildr.artifacts(project.test.compile.dependencies).
          select {|d| d.is_a?(ActsAsArtifact)}.
          select {|d| !done.include?(d.to_s)}.
          collect {|d| d.to_hash.merge(:scope => 'test', :include_transitive => include_transitive_deps.include?(d.to_s), :artifact => d)}

        xml.dependencies do
          deps.select {|dependency| project.pom.dependency_filter.nil? ? true : project.pom.dependency_filter.call(dependency)}.each do |dependency|
            xml.dependency do
              xml.groupId dependency[:group]
              xml.artifactId dependency[:id]
              xml.version dependency[:version]
              xml.classifier dependency[:classifier] if dependency[:classifier] && dependency[:classifier].to_s != 'jar'
              xml.scope dependency[:scope] unless dependency[:scope] == 'compile'
              xml.optional true if dependency[:optional]
              unless dependency[:include_transitive]
                xml.exclusions do
                  xml.exclusion do
                    xml.groupId '*'
                    xml.artifactId '*'
                  end
                end
              end
            end
          end
        end unless deps.empty?
      end
    end
  end
end
