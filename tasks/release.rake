require File.expand_path(File.dirname(__FILE__) + '/util')
require 'buildr/release_tool'

Buildr::ReleaseTool.define_release_task do |t|
  t.extract_version_from_changelog
  t.zapwhite
  t.ensure_git_clean
  t.verify_no_todo
  t.cleanup_staging
  t.build(:additional_tasks => "release_test_api_diff J2CL=#{ENV['J2CL']} STAGE_RELEASE=true")
  t.stage('ArchiveDownstream', 'Archive downstream projects that may need changes pushed') do
    unless ENV['DOWNSTREAM'] == 'no'
      FileUtils.rm_rf 'archive'
      FileUtils.mkdir_p 'archive'
      mv 'target/react4j_downstream-test/deploy_test/workdir', 'archive/downstream'
    end
  end
  t.patch_changelog('react4j/react4j',
                    :api_diff_directory => "#{WORKSPACE_DIR}/api-test",
                    :api_diff_website => 'https://react4j.github.io/api-diff?key=react4j&')
  t.stage('PatchWebsite', 'Update the website with a post announcing release') do
    setup_filename = 'docs/project_setup.md'
    IO.write(setup_filename, IO.read(setup_filename).
      gsub("<version>#{ENV['PREVIOUS_PRODUCT_VERSION']}</version>", "<version>#{ENV['PRODUCT_VERSION']}</version>"))
    sh 'git reset 2>&1 1> /dev/null'
    sh "git add #{setup_filename}"
    # Zapwhite only runs against files added to git so we have to do this dance after adding files
    `bundle exec zapwhite`
    sh 'git reset 2>&1 1> /dev/null'
    sh "git add #{setup_filename}"
    sh "git commit -m \"Update documentation to reflect the #{ENV['PRODUCT_VERSION']} release\""
  end
  t.stage('BuildWebsite', 'Build the website to ensure site still builds') do
    sh "bundle exec buildr clean react4j:dom:reactgen site:build TEST=no GWT=react4j:doc-examples#{Buildr.application.options.trace ? ' --trace' : ''}"
    task('site:link_check').invoke
  end
  t.tag_project
  t.stage('MavenCentralPublish', 'Publish artifacts to Maven Central') do
    sh "bundle exec buildr clean mcrt:publish_if_tagged site:deploy TEST=no GWT=react4j:doc-examples#{Buildr.application.options.trace ? ' --trace' : ''}"
  end
  t.patch_changelog_post_release
  t.stage('PatchStatisticsPostRelease', 'Copy the statistics forward to prepare for next development iteration') do
    filename = 'downstream-test/src/test/resources/fixtures/statistics.properties'
    current_version = ENV['PRODUCT_VERSION']
    next_version = Buildr::ReleaseTool.derive_next_version(current_version)
    pattern = /^#{current_version}\./

    lines = IO.read(filename).split("\n")
    lines +=
      lines
        .select { |line| line =~ pattern }
        .collect { |line| line.gsub("#{current_version}.", "#{next_version}.") }

    IO.write(filename, lines.sort.uniq.join("\n") + "\n")

    sh "git add #{filename}"
    sh 'git commit -m "Update statistics in preparation for next development iteration"'
  end

  t.push_changes
  t.github_release('react4j/react4j')
  t.stage('PushDownstreamChanges', 'Push downstream changes') do
    unless ENV['DOWNSTREAM'] == 'no'
      # Push the changes that have been made locally in downstream projects.
      # Artifacts have been pushed to staging repository by this time so they should build
      # even if it has not made it through the Maven release process

      DOWNSTREAM_EXAMPLES.each_pair do |downstream_example, branches|
        sh "cd archive/downstream/#{downstream_example} && git push --all"
        branches.each do |branch|
          full_branch = "#{branch}-React4jUpgrade-#{ENV['PRODUCT_VERSION']}"
          `cd archive/downstream/#{downstream_example} && git push origin :#{full_branch} 2>&1`
          puts "Completed remote branch #{downstream_example}/#{full_branch}. Removed." if 0 == $?.exitstatus
        end
      end

      DOWNSTREAM_PROJECTS.each do |downstream|
        # Need to extract the version from that project
        downstream_version = IO.read("archive/downstream/#{downstream}/CHANGELOG.md")[/^### \[v(\d+\.\d+)\]/, 1]
        sh "cd archive/downstream/#{downstream} && bundle exec buildr perform_release STAGE=MavenCentralPublish PREVIOUS_PRODUCT_VERSION= PRODUCT_VERSION=#{downstream_version}#{Buildr.application.options.trace ? ' --trace' : ''}"
        full_branch = "master-React4jUpgrade-#{ENV['PRODUCT_VERSION']}"
        `cd archive/downstream/#{downstream} && git push origin :#{full_branch} 2>&1`
        puts "Completed remote branch #{downstream}/#{full_branch}. Removed." if 0 == $?.exitstatus
      end

      FileUtils.rm_rf 'archive'
    end
  end
end
