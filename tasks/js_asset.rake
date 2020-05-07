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
      'com.unpkg:react:js:16.6.0' => {
        :path => 'react4j/public/dev/react.js',
        :regex_patches => {
          # See notes in React class why we need to do this
          /Fragment: REACT_FRAGMENT_TYPE,/ => "Fragment: REACT_FRAGMENT_TYPE,\n  Element: REACT_ELEMENT_TYPE,"
        }
      },
      'com.unpkg:react:js:min:16.6.0' => {
        :path => 'react4j/public/react.js',
        :regex_patches => {
          # See notes in React class why we need to do this
          /Fragment:q,/ => "Fragment:q,Element:y,"
        } }
    },
    :dom => {
      'com.unpkg:react-dom:js:16.6.0' => { :path => 'react4j/dom/public/dev/react-dom.js' },
      'com.unpkg:react-dom:js:min:16.6.0' => { :path => 'react4j/dom/public/react-dom.js' }
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
    group.each_pair do |spec, config|
      path = config[:path]
      target_filename = "#{base_js_resources_dir}/#{path}"
      copy_js_asset(spec, target_filename)
      if config[:regex_patches]
        content = IO.read(target_filename)
        config[:regex_patches].each_pair do |pattern, replacement|
          content.gsub!(pattern, replacement)
        end
        IO.write(target_filename, content)
      end
    end
  end
  task = project.file(base_js_resources_dir => [t.name])

  project.iml.main_generated_source_directories << base_js_resources_dir
  project.assets.paths << task
end
