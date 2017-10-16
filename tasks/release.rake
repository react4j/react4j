require File.expand_path(File.dirname(__FILE__) + '/util')

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
      ENV['PREVIOUS_PRODUCT_VERSION'] ||= changelog[/^### \[v(\d+\.\d+)\]/,1]

      next_version = ENV['PRODUCT_VERSION']
      unless next_version
        version_parts = ENV['PREVIOUS_PRODUCT_VERSION'].split('.')
        next_version = "#{version_parts[0]}.#{sprintf('%02d', version_parts[1].to_i + 1)}"
        ENV['PRODUCT_VERSION'] = next_version
      end

      # Also initialize release date if required
      ENV['RELEASE_DATE'] ||=  Time.now.strftime('%Y-%m-%d')
    end

    stage('ZapWhite', 'Ensure that zapwhite produces no changes') do
      sh 'bundle exec zapwhite'
    end

    stage('GitClean', 'Ensure there is nothing to commit and the working tree is clean') do
      status_output = `git status -s 2>&1`.strip
      raise 'Uncommitted changes in git repository. Please commit them prior to release.' if 0 != status_output.size
    end

    stage('Build', 'Build the project to ensure that the tests pass') do
      task('package').invoke
    end

    stage('PatchChangelog', 'Patch the changelog to update from previous release') do
      changelog = IO.read('CHANGELOG.md')
      changelog = changelog.gsub("### Unreleased\n",<<HEADER)
### [v#{ENV['PRODUCT_VERSION']}](https://github.com/realityforge/react4j/tree/v#{ENV['PRODUCT_VERSION']}) (#{ENV['RELEASE_DATE']})
[Full Changelog](https://github.com/realityforge/react4j/compare/v#{ENV['PREVIOUS_PRODUCT_VERSION']}...v#{ENV['PRODUCT_VERSION']})
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

      filename = "docs/_posts/#{ENV['RELEASE_DATE']}-version-#{ENV['PRODUCT_VERSION']}-release.md"
      IO.write(filename, <<CONTENT)
---
title: React4j #{ENV['PRODUCT_VERSION']} released
type: minor
---

[Full Changelog](https://github.com/realityforge/react4j/compare/v#{ENV['PREVIOUS_PRODUCT_VERSION']}...v#{ENV['PRODUCT_VERSION']})

Changes in this release:

#{changelog[start_index, end_index - start_index]}
CONTENT
      sh 'git reset 2>&1 1> /dev/null'
      sh "git add #{filename}"
      # Zapwhite only runs agains files added to git so we have to do this dance
      `bundle exec zapwhite`
      sh "git add #{filename}"
      sh "git commit -m \"Update site to add news about the #{ENV['PRODUCT_VERSION']} release\""
    end

    stage('BuildWebsite', 'Build the website to ensure site still builds') do
      task('site:build').invoke
      task('site:link_check').invoke
    end

    stage('TagProject', 'Tag the project') do
      sh "git tag v#{ENV['PRODUCT_VERSION']}"
    end

    stage('PatchChangelogPostRelease', 'Patch the changelog post release to prepare for next development iteration') do
      changelog = IO.read('CHANGELOG.md')
      changelog = changelog.gsub("# Change Log\n",<<HEADER)
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
    end
  end

  if ENV['STAGE']
    raise "Invalid STAGE specified '#{ENV['STAGE']}' that did not match any stage"
  end
end
