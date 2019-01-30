WORKSPACE_DIR = File.expand_path(File.dirname(__FILE__) + '/..')

DOWNSTREAM_PROJECTS=%w(react4j-windowportal)

def in_dir(dir)
  current = Dir.pwd
  begin
    Dir.chdir(dir)
    yield
  ensure
    Dir.chdir(current)
  end
end
