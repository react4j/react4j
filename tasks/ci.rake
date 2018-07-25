desc 'Continuous Integration task'
task 'ci' do
  contents = IO.read('CHANGELOG.md')
  previous_version = contents[/^### \[v(\d+\.\d+)\]/, 1]
  if contents =~ /^### Unreleased/
    version_parts = previous_version.split('.')
    version = "#{version_parts[0]}.#{sprintf('%02d', version_parts[1].to_i + 1)}"
  else
    version = previous_version
  end
  sh "bundle exec buildr package jacoco:report site:deploy mcrt:publish_if_tagged PRODUCT_VERSION=#{version} PREVIOUS_PRODUCT_VERSION=#{previous_version}"
end
