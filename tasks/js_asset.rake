JS_ASSETS =
  {
    'com.unpkg:react:js:16.6.0' => 'https://unpkg.com/react@16.6.0/umd/react.development.js',
    'com.unpkg:react:js:min:16.6.0' => 'https://unpkg.com/react@16.6.0/umd/react.production.min.js',
    'com.unpkg:react-dom:js:16.6.0' => 'https://unpkg.com/react-dom@16.6.0/umd/react-dom.development.js',
    'com.unpkg:react-dom:js:min:16.6.0' => 'https://unpkg.com/react-dom@16.6.0/umd/react-dom.production.min.js',
  }

JS_ASSET_GROUPS =
  {
    :core => {
      'com.unpkg:react:js:16.6.0' => 'react4j/public/dev/react.js',
      'com.unpkg:react:js:min:16.6.0' => 'react4j/public/react.js'
    },
    :dom => {
      'com.unpkg:react-dom:js:16.6.0' => 'react4j/dom/public/dev/react-dom.js',
      'com.unpkg:react-dom:js:min:16.6.0' => 'react4j/dom/public/react-dom.js'
    }
  }

def copy_js_asset(spec, target_filename)
  url = JS_ASSETS[spec]
  raise "Unable to locate url for spec #{spec}" unless url
  artifact = Buildr.artifact(spec)
  Buildr.download(artifact => url)
  artifact.invoke
  FileUtils.mkdir_p File.dirname(target_filename)
  FileUtils.cp artifact.to_s, target_filename
end

def js_assets(project, group_name)
  base_js_resources_dir = project._(:generated, :js_assets, :src, :main, :resources)
  group = JS_ASSET_GROUPS[group_name] || (raise "Unable to locate group #{group_name}")
  desc 'Copy Javascript assets to generated dir'
  t = project.task('js_resources') do
    group.each_pair do |spec, target|
      copy_js_asset(spec, "#{base_js_resources_dir}/#{target}")
    end
  end
  task = project.file(base_js_resources_dir => [t.name])

  project.iml.main_generated_source_directories << base_js_resources_dir
  project.assets.paths << task
end
