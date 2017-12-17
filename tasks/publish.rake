require 'mcrt'

desc 'Publish release on maven central'
task 'publish_to_maven_central' do
  project = Buildr.projects[0].root_project
  password = ENV['MAVEN_CENTRAL_PASSWORD'] || (raise "Unable to locate environment variable with name 'MAVEN_CENTRAL_PASSWORD'")
  MavenCentralPublishTool.buildr_release(project, 'org.realityforge', 'realityforge', password)
end

def get_head_tag_if_any
  version = `git describe --exact-match --tags 2>&1`
  if 0 == $?.exitstatus && version =~ /^v[0-9]/ && (ENV['TRAVIS_BUILD_ID'].nil? || ENV['TRAVIS_TAG'].to_s != '')
    version.strip
  else
    nil
  end
end

def is_tag_on_branch?(tag, branch)
  output = `git tag --merged #{branch} 2>&1`
  tags = output.split
  tags.include?(tag)
end

def is_tag_on_candidate_branches?(tag, branches)
  branches.each do |branch|
    return true if is_tag_on_branch?(tag, branch)
  end
  false
end

desc 'Publish release to maven central iff current HEAD is a tag'
task 'publish_if_tagged' do
  candidate_branches = %w(master)
  tag = get_head_tag_if_any
  if !tag.nil? && is_tag_on_candidate_branches?(tag, candidate_branches)
    task('publish_to_maven_central').invoke
  end
end
