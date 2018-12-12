require File.expand_path(File.dirname(__FILE__) + '/util')

ENV['PREVIOUS_PRODUCT_VERSION'] = nil if ENV['PREVIOUS_PRODUCT_VERSION'].to_s == ''
ENV['PRODUCT_VERSION'] = nil if ENV['PRODUCT_VERSION'].to_s == ''

def stage(stage_name, description, options = {})
  if ENV['STAGE'].nil? || ENV['STAGE'] == stage_name || options[:always_run]
    puts "🚀 Release Stage: #{stage_name} - #{description}"
    begin
      yield
    rescue Exception => e
      puts '💣 Error completing stage.'
      puts "Fix the error and re-run release process passing: STAGE=#{stage_name}#{ ENV['PREVIOUS_PRODUCT_VERSION'] ? " PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']}" : ''}#{ ENV['PREVIOUS_PRODUCT_VERSION'] ? " PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']}" : ''}"
      raise e
    end
    ENV['STAGE'] = nil unless options[:always_run]
  elsif !ENV['STAGE'].nil?
    puts "Skipping Stage: #{stage_name} - #{description}"
  end
end

desc 'Perform a release'
task 'perform_release' do

  in_dir(WORKSPACE_DIR) do
    stage('ExtractVersion', 'Extract the last version from CHANGELOG.md and derive next version unless specified', :always_run => true) do
      changelog = IO.read('CHANGELOG.md')
      ENV['PREVIOUS_PRODUCT_VERSION'] ||= changelog[/^### \[v(\d+\.\d+)\]/, 1]

      next_version = ENV['PRODUCT_VERSION']
      unless next_version
        version_parts = ENV['PREVIOUS_PRODUCT_VERSION'].split('.')
        next_version = "#{version_parts[0]}.#{sprintf('%02d', version_parts[1].to_i + 1)}"
        ENV['PRODUCT_VERSION'] = next_version
      end

      # Also initialize release date if required
      ENV['RELEASE_DATE'] ||= Time.now.strftime('%Y-%m-%d')
    end

    stage('ZapWhite', 'Ensure that zapwhite produces no changes') do
      sh 'bundle exec zapwhite'
    end

    stage('GitClean', 'Ensure there is nothing to commit and the working tree is clean') do
      status_output = `git status -s 2>&1`.strip
      raise 'Uncommitted changes in git repository. Please commit them prior to release.' if 0 != status_output.size
    end

    stage('StagingCleanup', 'Remove artifacts from staging repository') do
      task('staging:cleanup').invoke
    end

    stage('Build', 'Build the project to ensure that the tests pass') do
      sh "bundle exec buildr clean package PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} STAGE_RELEASE=true#{ENV['TEST'].nil? ? '' : " TEST=#{ENV['TEST']}"}#{Buildr.application.options.trace ? ' --trace' : ''}"
    end

    stage('ArchiveDownstream', 'Archive downstream projects that may need changes pushed') do
      FileUtils.rm_rf 'archive'
      FileUtils.mkdir_p 'archive'
      mv 'target/react4j_downstream-test/deploy_test/workdir', 'archive/downstream'
    end

    stage('PatchChangelog', 'Patch the changelog to update from previous release') do
      changelog = IO.read('CHANGELOG.md')
      changelog = changelog.gsub("### Unreleased\n", <<HEADER)
### [v#{ENV['PRODUCT_VERSION']}](https://github.com/react4j/react4j/tree/v#{ENV['PRODUCT_VERSION']}) (#{ENV['RELEASE_DATE']})
[Full Changelog](https://github.com/react4j/react4j/compare/v#{ENV['PREVIOUS_PRODUCT_VERSION']}...v#{ENV['PRODUCT_VERSION']})
HEADER
      IO.write('CHANGELOG.md', changelog)

      sh 'git reset 2>&1 1> /dev/null'
      sh 'git add CHANGELOG.md'
      sh 'git commit -m "Update CHANGELOG.md in preparation for release"'
    end

    stage('PatchWebsite', 'Update the website with a post announcing release') do
      changelog = IO.read('CHANGELOG.md')

      # Find the double new line after the product version banner
      start_index = changelog.index("\n\n", changelog.index("## [v#{ENV['PRODUCT_VERSION']}]")) + 2

      end_index = changelog.index("### [v#{ENV['PREVIOUS_PRODUCT_VERSION']}]", start_index)

      filename = "website/blog/#{ENV['RELEASE_DATE']}-version-#{ENV['PRODUCT_VERSION']}-release.md"
      IO.write(filename, <<CONTENT)
---
title: React4j #{ENV['PRODUCT_VERSION']} released
author: React4j Project
authorURL: https://github.com/react4j
---

[Full Changelog](https://github.com/react4j/react4j/compare/v#{ENV['PREVIOUS_PRODUCT_VERSION']}...v#{ENV['PRODUCT_VERSION']})

Changes in this release:

#{changelog[start_index, end_index - start_index].gsub('https://react4j.github.io', '')}
CONTENT
      setup_filename = 'docs/project_setup.md'
      IO.write(setup_filename, IO.read(setup_filename).
        gsub("<version>#{ENV['PREVIOUS_PRODUCT_VERSION']}</version>", "<version>#{ENV['PRODUCT_VERSION']}</version>"))
      sh 'git reset 2>&1 1> /dev/null'
      sh "git add #{filename}"
      sh "git add #{setup_filename}"
      # Zapwhite only runs against files added to git so we have to do this dance after adding files
      `bundle exec zapwhite`
      sh 'git reset 2>&1 1> /dev/null'
      sh "git add #{filename}"
      sh "git commit -m \"Update site to add news about the #{ENV['PRODUCT_VERSION']} release\""
      sh "git add #{setup_filename}"
      sh "git commit -m \"Update documentation to reflect the #{ENV['PRODUCT_VERSION']} release\""
    end

    stage('BuildWebsite', 'Build the website to ensure site still builds') do
      task('site:build').invoke
      task('site:link_check').invoke
    end

    stage('TagProject', 'Tag the project') do
      sh "git tag v#{ENV['PRODUCT_VERSION']}"
    end

    stage('MavenCentralPublish', 'Publish artifacts to Maven Central') do
      sh 'buildr clean mcrt:publish_if_tagged site:deploy TEST=no GWT=no'
    end

    stage('PatchChangelogPostRelease', 'Patch the changelog post release to prepare for next development iteration') do
      changelog = IO.read('CHANGELOG.md')
      changelog = changelog.gsub("# Change Log\n", <<HEADER)
# Change Log

### Unreleased
HEADER
      IO.write('CHANGELOG.md', changelog)

      `bundle exec zapwhite`
      sh 'git add CHANGELOG.md'
      sh 'git commit -m "Update CHANGELOG.md in preparation for next development iteration"'
    end

    stage('PushChanges', 'Push changes to git repository') do
      sh 'git push'
      sh 'git push --tags'
      # Push the changes that have been made locally in downstream projects.
      # Artifacts have been pushed to staging repository by this time so they should build
      # even if it has not made it through the Maven release process
      sh 'cd archive/downstream/react4j-todomvc && git push --all'
      %w(raw raw_maven arez arez_maven dagger dagger_maven raw_maven_j2cl arez_maven_j2cl dagger_maven_j2cl).each do |branch|
        full_branch = "#{branch}-ArezUpgrade-#{ENV['PRODUCT_VERSION']}"
        `cd archive/downstream/react4j-todomvc && git push origin :#{full_branch} 2>&1`
        puts "Completed remote branch #{full_branch}. Removed." if 0 == $?.exitstatus
      end

      DOWNSTREAM_PROJECTS.each do |downstream|
        # Need to extract the version from that project
        downstream_version = IO.read("archive/downstream/#{downstream}/CHANGELOG.md")[/^### \[v(\d+\.\d+)\]/, 1]
        sh "cd archive/downstream/#{downstream} && bundle exec buildr perform_release STAGE=StageRelease PREVIOUS_PRODUCT_VERSION= PRODUCT_VERSION=#{downstream_version}#{Buildr.application.options.trace ? ' --trace' : ''}"
        full_branch = "master-ArezUpgrade-#{ENV['PRODUCT_VERSION']}"
        `cd archive/downstream/#{downstream} && git push origin :#{full_branch} 2>&1`
        puts "Completed remote branch #{full_branch}. Removed." if 0 == $?.exitstatus
      end

      FileUtils.rm_rf 'archive'
    end

    stage('GithubRelease', 'Create a Release on GitHub') do
      changelog = IO.read('CHANGELOG.md')
      start = changelog.index("### [v#{ENV['PRODUCT_VERSION']}]")
      raise "Unable to locate version #{ENV['PRODUCT_VERSION']} in change log" if -1 == start
      start = changelog.index("\n", start)
      start = changelog.index("\n", start + 1)

      end_index = changelog.index('### [v', start)

      changes = changelog[start, end_index - start]

      changes = changes.strip

      tag = "v#{ENV['PRODUCT_VERSION']}"

      require 'octokit'

      client = Octokit::Client.new(:netrc => true, :auto_paginate => true)
      client.login
      client.create_release('react4j/react4j', tag, :name => tag, :body => changes, :draft => false, :prerelease => true)

      candidates = client.list_milestones('react4j/react4j').select {|m| m[:title].to_s == tag}
      unless candidates.empty?
        milestone = candidates[0]
        unless milestone[:state] == 'closed'
          client.update_milestone('react4j/react4j', milestone[:number], :state => 'closed')
        end
      end
    end
  end

  if ENV['STAGE']
    raise "Invalid STAGE specified '#{ENV['STAGE']}' that did not match any stage"
  end
end
