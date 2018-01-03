require File.expand_path(File.dirname(__FILE__) + '/util')

SITE_DIR = "#{WORKSPACE_DIR}/reports/site"

desc 'Copy the javadocs to docs dir'
task 'site:javadocs' do
  javadocs_dir = "#{WORKSPACE_DIR}/target/react4j/doc"
  file(javadocs_dir).invoke
  mkdir_p SITE_DIR
  cp_r javadocs_dir, "#{SITE_DIR}/api"
end

desc 'Copy the compiled examples to docs dir'
task 'site:examples' do
  project = Buildr.project('react4j:doc-examples')
  examples_dir = project._(:target, :generated, :gwt, 'react4j-doc-examples')
  file(examples_dir).invoke
  mkdir_p SITE_DIR
  rm_rf "#{SITE_DIR}/examples"
  cp_r examples_dir, "#{SITE_DIR}/examples"
  rm_f Dir["#{SITE_DIR}/examples/**/*.devmode.js"]
  rm_f Dir["#{SITE_DIR}/examples/**/compilation-mappings.txt"]
  rm_rf "#{SITE_DIR}/examples/WEB-INF"

  public_js = Dir["#{SITE_DIR}/examples/*/*.js"].select do |filename|
    !(filename =~ /.*\.cache.js$/ || filename =~ /.*\.nocache.js$/)
  end
  mv public_js, "#{SITE_DIR}/examples"

  EXAMPLES.keys.each do |name|
    content = IO.read(project._("src/main/webapp/#{name}.html"))
    content = content.gsub("http://127.0.0.1:8888/#{name}/dev/", '').gsub('http://127.0.0.1:8888/', '')
    IO.write("#{SITE_DIR}/examples/#{name}.html", content)
  end
end

desc 'Copy the favicons to docs dir'
task 'site:favicons' do
  # The favicons were generated via https://www.favicon-generator.org/ but there
  # is a lot of icons that are generated from tool so we put it in a subdirectory
  # and copy to the site as part of the build
  favicons_dir = "#{WORKSPACE_DIR}/docs/_favicons"
  mkdir_p SITE_DIR
  cp_r Dir["#{favicons_dir}/*"], SITE_DIR
end

desc 'Build the website'
task 'site:build' do
  rm_rf SITE_DIR
  mkdir_p File.dirname(SITE_DIR)
  sh "jekyll build --source #{WORKSPACE_DIR}/docs --destination #{SITE_DIR}"
  task('site:javadocs').invoke
  task('site:favicons').invoke
  task('site:examples').invoke
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

  webserver = WEBrick::HTTPServer.new(:Port => port, :DocumentRoot => SITE_DIR)
  Thread.new {webserver.start}

  trap('INT') {webserver.shutdown}
  begin
    sh "yarn blc --ordered --recursive  --filter-level 3 http://#{address}:#{port} --exclude https://github.com/react4j/react4j/compare/ --exclude https://medium.freecodecamp.org/ --exclude https://docs.oracle.com/javase/8/docs/api"
  ensure
    webserver.shutdown
  end
end

desc 'Serve the website for developing documentation'
task 'site:serve' do
  mkdir_p File.dirname(SITE_DIR)
  sh "jekyll serve --source #{WORKSPACE_DIR}/docs --destination #{SITE_DIR}"
end

desc 'Build the website'
task 'site:deploy' => ['site:build'] do
  # Verify the site is valid first
  task('site:link_check').invoke

  # Only publish the site off the master branch if running out of Travis
  if ENV['TRAVIS_BRANCH'].nil? || ENV['TRAVIS_BRANCH'] == 'master'
    origin_url = 'https://github.com/react4j/react4j.github.io.git'

    travis_build_number = ENV['TRAVIS_BUILD_NUMBER']
    if travis_build_number
      origin_url = origin_url.gsub('https://github.com/', 'git@github.com:')
    end

    local_dir = "#{WORKSPACE_DIR}/targOnDeactivateet/remote_site"
    rm_rf local_dir

    sh "git clone -b master #{origin_url} #{local_dir}"

    # This is the list of directories controlled by other processes that should be left alone
    excludes = %w(todomvc)

    in_dir(local_dir) do
      message = "Publish website#{travis_build_number.nil? ? '' : " - Travis build: #{travis_build_number}"}"

      rm_rf Dir["#{local_dir}/*"].select {|f| !excludes.include?(File.basename(f))}
      cp_r Dir["#{SITE_DIR}/*"], local_dir
      sh 'git add . -f'
      unless `git status -s`.strip.empty?
        sh "git commit -m \"#{message}\""
        sh 'git push -f origin master'
      end
    end
  end
end
