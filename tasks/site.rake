WORKSPACE_DIR = File.expand_path(File.dirname(__FILE__) + '/..')
SITE_DIR = "#{WORKSPACE_DIR}/reports/site/arez"

desc 'Copy the javadocs to docs dir'
task 'site:javadocs' do
  javadocs_dir = "#{WORKSPACE_DIR}/target/arez/doc"
  file(javadocs_dir).invoke
  mkdir_p SITE_DIR
  cp_r javadocs_dir, "#{SITE_DIR}/api"
end

desc 'Build the website'
task 'site:build' do
  rm_rf SITE_DIR
  mkdir_p File.dirname(SITE_DIR)
  sh "jekyll build --source #{WORKSPACE_DIR}/docs --destination #{SITE_DIR}"
  task('site:javadocs').invoke
end

desc 'Check that the website does not have any broken links'
task 'site:link_check' do
  require 'webrick'
  require 'socket'

  # Get a free port and web address
  socket = Socket.new(:INET, :STREAM, 0)
  socket.bind(Addrinfo.tcp('127.0.0.1', 0))
  address = socket.local_address.ip_address
  port = socket.local_address.ip_port
  socket.close

  webserver = WEBrick::HTTPServer.new(:Port => port, :DocumentRoot => File.dirname(SITE_DIR))
  Thread.new {webserver.start}

  trap('INT') {webserver.shutdown}
  begin
    sh "yarn blc --ordered --recursive  --filter-level 3 http://#{address}:#{port}/arez"
    # It does not follow frames in javadocs so run a separate pass over page that checks all javadocs
    sh "yarn blc --ordered --recursive  --filter-level 3 http://#{address}:#{port}/arez/api/index-all.html"
  ensure
    webserver.shutdown
  end
end

desc 'Serve the website for developing documentation'
task 'site:serve' do
  rm_rf SITE_DIR
  mkdir_p File.dirname(SITE_DIR)
  sh "jekyll serve --source #{WORKSPACE_DIR}/docs --destination #{SITE_DIR}"
end

def in_dir(dir)
  current = Dir.pwd
  begin
    Dir.chdir(dir)
    yield
  ensure
    Dir.chdir(current)
  end
end

desc 'Build the website'
task 'site:deploy' => ['site:build'] do
  # Verify the site is valid first
  task('site:link_check').invoke

  origin_url = `git remote get-url origin`

  travis_build_number = ENV['TRAVIS_BUILD_NUMBER']
  if travis_build_number
    origin_url = origin_url.gsub('https://github.com/', 'git@github.com:')
  end

  in_dir(SITE_DIR) do
    sh 'git init'
    sh 'git add .'
    message =
      travis_build_number.nil? ?
        'Publish website' :
        "Publish website - Travis build: #{travis_build_number}"

    sh "git commit -m \"#{message}\""
    sh "git remote add origin #{origin_url}"
    sh 'git push -f origin master:gh-pages'
  end
end
