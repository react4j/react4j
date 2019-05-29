desc 'Cleanup staging artifacts for this project'
task 'staging:cleanup' do
  cleanup_staging_repository
end

desc 'Cleanup staging artifacts for all projects'
task 'staging:cleanup:all' do
  cleanup_staging_repository(true)
end

def cleanup_staging_repository(include_all_artifacts = false)
  require 'net/https'

  relative_group_path = Buildr.projects[0].root_project.group.gsub(/^org\.realityforge/,'').gsub('.','/')
  staging_location = "https://stocksoftware.jfrog.io/stocksoftware/staging/org/realityforge#{include_all_artifacts ? '' : relative_group_path}"
  uri = URI(staging_location)
  res = Net::HTTP.start(uri.hostname, uri.port, :use_ssl => uri.scheme == 'https') do |http|
    req = Net::HTTP::Delete.new(uri)
    req.basic_auth ENV['STAGING_USERNAME'], ENV['STAGING_PASSWORD']
    http.request(req)
  end

  if !res.is_a?(Net::HTTPNoContent) && !res.is_a?(Net::HTTPNotFound)
    puts "Failed removing staging artifacts from #{staging_location}"
    puts "Error: #{res.code}: #{res.message}"
    exit 42
  end
end
