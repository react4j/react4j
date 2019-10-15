raise "Patch applied upstream " if Buildr::VERSION.to_s > '1.5.8'

class Buildr::TestNG < TestFramework::Java

  def run(tests, dependencies) #:nodoc:
    cmd_args = []
    cmd_args << '-suitename' << task.project.id
    cmd_args << '-sourcedir' << task.compile.sources.join(';') if TestNG.version < '6.0'
    cmd_args << '-log' << '2'
    cmd_args << '-d' << task.report_to.to_s
    exclude_args = options[:excludegroups] || []
    unless exclude_args.empty?
      cmd_args << '-excludegroups' << exclude_args.join(',')
    end
    groups_args = options[:groups] || []
    unless groups_args.empty?
      cmd_args << '-groups' << groups_args.join(',')
    end
    # run all tests in the same suite
    cmd_args << '-testclass' << (TestNG.version < '6.0' ? test : tests.join(','))

    cmd_args += options[:args] if options[:args]
    cmd_options = { :properties => options[:properties], :java_args => options[:java_args],
                    :classpath => dependencies, :name => "TestNG in #{task.send(:project).name}" }

    tmp = nil
    begin
      tmp = Tempfile.open('testNG')
      tmp.write cmd_args.join("\n")
      tmp.close
      Java::Commands.java ['org.testng.TestNG', "@#{tmp.path}"], cmd_options
    ensure
      tmp.close unless tmp.nil?
    end
    # testng-failed.xml contains the list of failed tests *only*
    failed_tests = File.join(task.report_to.to_s, 'testng-failed.xml')
    if File.exist?(failed_tests)
      report = File.read(failed_tests)
      failed = report.scan(/<class name="(.*?)">/im).flatten
      # return the list of passed tests
      return tests - failed
    else
      return tests
    end
  end
end

