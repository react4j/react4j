WORKSPACE_DIR = File.expand_path(File.dirname(__FILE__) + '/..')

def in_dir(dir)
  current = Dir.pwd
  begin
    Dir.chdir(dir)
    yield
  ensure
    Dir.chdir(current)
  end
end
