desc 'Update the api_differences for the next version'
task 'update_api_diff' do
  derive_versions

  sh "buildr clean react4j:api-test:test TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_API_DIFF=true"
end

desc 'Test the api differences for the next version'
task 'test_api_diff' do
  derive_versions

  sh "buildr clean react4j:api-test:test TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']}"
end
