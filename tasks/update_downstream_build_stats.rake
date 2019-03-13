def derive_versions
  changelog = IO.read('CHANGELOG.md')
  ENV['PREVIOUS_PRODUCT_VERSION'] ||= changelog[/^### \[v(\d+\.\d+)\]/, 1]

  next_version = ENV['PRODUCT_VERSION']
  unless next_version
    version_parts = ENV['PREVIOUS_PRODUCT_VERSION'].split('.')
    next_version = "#{version_parts[0]}.#{sprintf('%02d', version_parts[1].to_i + 1)}"
    ENV['PRODUCT_VERSION'] = next_version
  end
end

desc 'Update the statistics stored for next version'
task 'update_downstream_build_stats' do
  derive_versions

  sh "buildr clean react4j:downstream-test:test:compile DOWNSTREAM=no TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end

desc 'Test the statistics stored for next version'
task 'test_downstream_build_stats' do
  derive_versions

  sh "buildr clean react4j:downstream-test:test DOWNSTREAM=no TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']}"
end
