WORKSPACE_DIR = File.expand_path(File.dirname(__FILE__) + '/..')

DOWNSTREAM_PROJECTS=%w(react4j-windowportal)

# Project -> [Branch1, Branch2, ...]
DOWNSTREAM_EXAMPLES =
  {
    'react4j-todomvc' => %w(raw raw_maven arez arez_maven spritz dagger dagger_maven raw_maven_j2cl arez_maven_j2cl dagger_maven_j2cl),
    'react4j-drumloop' => %w(master),
    'react4j-flux-challenge' => %w(master),
  }

def in_dir(dir)
  current = Dir.pwd
  begin
    Dir.chdir(dir)
    yield
  ensure
    Dir.chdir(current)
  end
end
