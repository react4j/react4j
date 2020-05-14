require 'buildr/gwt'

module Buildr
  module GWT
    class << self
      # The specs for requirements
      def dependencies(version = nil)
        validation_deps =
            %w(javax.validation:validation-api:jar:1.0.0.GA javax.validation:validation-api:jar:sources:1.0.0.GA)
        v = version || self.version
        gwt_dev_jar = "com.google.gwt:gwt-dev:jar:#{v}"
        if v <= '2.6.1'
          [gwt_dev_jar] + validation_deps
        elsif v == '2.7.0'
          [
              gwt_dev_jar,
              'org.ow2.asm:asm:jar:5.0.3'
          ] + validation_deps
        elsif v == '2.8.0'
          %w(
              com.google.jsinterop:jsinterop-annotations:jar:1.0.1
              com.google.jsinterop:jsinterop-annotations:jar:sources:1.0.1
              org.w3c.css:sac:jar:1.3
              com.google.gwt:gwt-dev:jar:2.8.0
              com.google.gwt:gwt-user:jar:2.8.0
              com.google.code.gson:gson:jar:2.6.2
              org.ow2.asm:asm:jar:5.0.3
              org.ow2.asm:asm-util:jar:5.0.3
              org.ow2.asm:asm-tree:jar:5.0.3
              org.ow2.asm:asm-commons:jar:5.0.3
              colt:colt:jar:1.2.0
              ant:ant:jar:1.6.5
              commons-collections:commons-collections:jar:3.2.2
              commons-io:commons-io:jar:2.4
              com.ibm.icu:icu4j:jar:50.1.1
              tapestry:tapestry:jar:4.0.2

              javax.annotation:javax.annotation-api:jar:1.2
              javax.servlet:javax.servlet-api:jar:3.1.0
              org.eclipse.jetty:jetty-annotations:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-continuation:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-http:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-io:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-jndi:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-plus:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-security:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-server:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlet:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlets:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-util:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-webapp:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-xml:jar:9.2.14.v20151106
              org.eclipse.jetty.toolchain:jetty-schemas:jar:3.1.M0
          ) + validation_deps
        elsif v == '2.8.1'
          %w(
              com.google.jsinterop:jsinterop-annotations:jar:1.0.1
              com.google.jsinterop:jsinterop-annotations:jar:sources:1.0.1
              org.w3c.css:sac:jar:1.3
              com.google.gwt:gwt-dev:jar:2.8.1
              com.google.gwt:gwt-user:jar:2.8.1
              com.google.code.gson:gson:jar:2.6.2
              org.ow2.asm:asm:jar:5.0.3
              org.ow2.asm:asm-util:jar:5.0.3
              org.ow2.asm:asm-tree:jar:5.0.3
              org.ow2.asm:asm-commons:jar:5.0.3
              colt:colt:jar:1.2.0
              ant:ant:jar:1.6.5
              commons-collections:commons-collections:jar:3.2.2
              commons-io:commons-io:jar:2.4
              com.ibm.icu:icu4j:jar:50.1.1
              tapestry:tapestry:jar:4.0.2

              javax.annotation:javax.annotation-api:jar:1.2
              javax.servlet:javax.servlet-api:jar:3.1.0
              org.eclipse.jetty:jetty-annotations:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-continuation:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-http:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-io:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-jndi:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-plus:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-security:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-server:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlet:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlets:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-util:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-webapp:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-xml:jar:9.2.14.v20151106
              org.eclipse.jetty.toolchain:jetty-schemas:jar:3.1.M0
          ) + validation_deps
        elsif v == '2.8.2'
          %w(
              com.google.jsinterop:jsinterop-annotations:jar:1.0.2
              com.google.jsinterop:jsinterop-annotations:jar:sources:1.0.2
              org.w3c.css:sac:jar:1.3
              com.google.gwt:gwt-dev:jar:2.8.2
              com.google.gwt:gwt-user:jar:2.8.2
              com.google.code.gson:gson:jar:2.6.2
              org.ow2.asm:asm:jar:5.0.3
              org.ow2.asm:asm-util:jar:5.0.3
              org.ow2.asm:asm-tree:jar:5.0.3
              org.ow2.asm:asm-commons:jar:5.0.3
              colt:colt:jar:1.2.0
              ant:ant:jar:1.6.5
              commons-collections:commons-collections:jar:3.2.2
              commons-io:commons-io:jar:2.4
              com.ibm.icu:icu4j:jar:50.1.1
              tapestry:tapestry:jar:4.0.2

              javax.annotation:javax.annotation-api:jar:1.2
              javax.servlet:javax.servlet-api:jar:3.1.0
              org.eclipse.jetty:jetty-annotations:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-continuation:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-http:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-io:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-jndi:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-plus:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-security:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-server:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlet:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlets:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-util:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-webapp:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-xml:jar:9.2.14.v20151106
              org.eclipse.jetty.toolchain:jetty-schemas:jar:3.1.M0
          ) + validation_deps
        elsif v == '2.8.2-v20191108'
          %w(
              org.realityforge.com.google.jsinterop:jsinterop-annotations:jar:2.8.2-v20191108
              org.realityforge.com.google.jsinterop:jsinterop-annotations:jar:sources:2.8.2-v20191108
              org.w3c.css:sac:jar:1.3
              org.realityforge.com.google.gwt:gwt-dev:jar:2.8.2-v20191108
              org.realityforge.com.google.gwt:gwt-user:jar:2.8.2-v20191108
              com.google.code.gson:gson:jar:2.6.2

              org.ow2.asm:asm:jar:7.1
              org.ow2.asm:asm-util:jar:7.1
              org.ow2.asm:asm-tree:jar:7.1
              org.ow2.asm:asm-commons:jar:7.1

              colt:colt:jar:1.2.0
              ant:ant:jar:1.6.5
              commons-collections:commons-collections:jar:3.2.2
              commons-io:commons-io:jar:2.4
              com.ibm.icu:icu4j:jar:63.1
              tapestry:tapestry:jar:4.0.2

              javax.annotation:javax.annotation-api:jar:1.2
              javax.servlet:javax.servlet-api:jar:3.1.0
              org.eclipse.jetty:jetty-annotations:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-continuation:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-http:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-io:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-jndi:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-plus:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-security:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-server:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlet:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlets:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-util:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-webapp:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-xml:jar:9.2.14.v20151106
              org.eclipse.jetty.toolchain:jetty-schemas:jar:3.1.M0
          ) + validation_deps
        elsif v == '2.9.0'
          %w(
              org.realityforge.com.google.jsinterop:jsinterop-annotations:jar:2.8.2-v20191108
              org.realityforge.com.google.jsinterop:jsinterop-annotations:jar:sources:2.8.2-v20191108
              org.w3c.css:sac:jar:1.3

              com.google.gwt:gwt-dev:jar:2.9.0
              com.google.gwt:gwt-user:jar:2.9.0
              com.google.code.gson:gson:jar:2.6.2

              org.ow2.asm:asm:jar:7.1
              org.ow2.asm:asm-util:jar:7.1
              org.ow2.asm:asm-tree:jar:7.1
              org.ow2.asm:asm-commons:jar:7.1

              colt:colt:jar:1.2.0
              ant:ant:jar:1.6.5
              commons-collections:commons-collections:jar:3.2.2
              commons-io:commons-io:jar:2.4
              com.ibm.icu:icu4j:jar:63.1
              tapestry:tapestry:jar:4.0.2

              javax.annotation:javax.annotation-api:jar:1.2
              javax.servlet:javax.servlet-api:jar:3.1.0
              org.eclipse.jetty:jetty-annotations:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-continuation:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-http:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-io:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-jndi:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-plus:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-security:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-server:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlet:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-servlets:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-util:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-webapp:jar:9.2.14.v20151106
              org.eclipse.jetty:jetty-xml:jar:9.2.14.v20151106
              org.eclipse.jetty.toolchain:jetty-schemas:jar:3.1.M0
          ) + validation_deps
        else
          raise "Unknown GWT version #{v}"
        end
      end
    end

    module ProjectExtension
      protected
      def gwt_detect_version(dependencies)
        version = nil
        dependencies.each do |dep|
          if dep.respond_to?(:to_spec_hash)
            hash = dep.to_spec_hash
            if %w(org.realityforge.com.google.gwt com.google.gwt).include?(hash[:group]) && 'gwt-user' == hash[:id] && :jar == hash[:type]
              version = hash[:version]
            end
          end
        end
        version
      end
    end
  end
end
