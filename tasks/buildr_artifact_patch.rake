raise 'Patch already integrated into buildr code' unless Buildr::VERSION.to_s == '1.5.5'

module Buildr::ActsAsArtifact
  def upload_task(upload_to = nil)
    upload_to ||= Buildr.repositories.snapshot_to if snapshot? && Buildr.repositories.snapshot_to != nil && Buildr.repositories.snapshot_to[:url] != nil
    upload_to ||= Buildr.repositories.release_to
    upload_to = { :url => upload_to } unless Hash === upload_to
    raise ArgumentError, 'Don\'t know where to upload, perhaps you forgot to set repositories.release_to' unless upload_to[:url]

    # Set the upload URI, including mandatory slash (we expect it to be the base directory).
    # Username/password may be part of URI, or separate entities.
    uri = URI.parse(upload_to[:url].clone)
    uri.path = uri.path + '/' unless uri.path[-1] == '/'
    to_escape = "!\"\#$%&'()*+,-./:;<=>?@{}|~`'"
    uri.user = URI.encode(upload_to[:username], to_escape) if upload_to[:username]
    uri.password = URI.encode(upload_to[:password], to_escape) if upload_to[:password]

    path = group.gsub('.', '/') + "/#{id}/#{version}/#{upload_name}"

    unless task = Buildr.application.lookup(uri+path)
      deps = [self]
      deps << pom.upload_task(upload_to) if pom && pom != self && classifier.nil?

      task = Rake::Task.define_task uri + path => deps do
        # Upload artifact relative to base URL, need to create path before uploading.
        options = upload_to[:options] || { :permissions => upload_to[:permissions] }
        info "Deploying #{to_spec}"
        URI.upload uri + path, name, options
        if snapshot? && pom != self
          maven_metadata = group.gsub('.', '/') + "/#{id}/#{version}/#{MAVEN_METADATA}"
          URI.write uri + maven_metadata, maven_metadata_xml, :permissions => upload_to[:permissions]
        end
      end
    end
    task
  end
end

class URI::HTTP
  private

  def write_internal(options, &block)
    options ||= {}
    connect do |http|
      trace "Uploading to #{path}"
      content = StringIO.new
      while chunk = yield(RW_CHUNK_SIZE)
        content << chunk
      end
      headers = { 'Content-MD5' => Digest::MD5.hexdigest(content.string), 'Content-Type' => 'application/octet-stream', 'User-Agent' => "Buildr-#{Buildr::VERSION}" }
      request = Net::HTTP::Put.new(request_uri.empty? ? '/' : request_uri, headers)
      request.basic_auth URI.decode(self.user), URI.decode(self.password) if self.user
      response = nil
      with_progress_bar options[:progress], path.split('/').last, content.size do |progress|
        request.content_length = content.size
        content.rewind
        stream = Object.new
        class << stream;
          self;
        end.send :define_method, :read do |*args|
          bytes = content.read(*args)
          progress << bytes if bytes
          bytes
        end
        request.body_stream = stream
        response = http.request(request)
      end

      case response
        when Net::HTTPRedirection
          # Try to download from the new URI, handle relative redirects.
          trace "Redirected to #{response['Location']}"
          content.rewind
          return (self + URI.parse(response['location'])).write_internal(options) {|bytes| content.read(bytes)}
        when Net::HTTPSuccess
        else
          raise RuntimeError, "Failed to upload #{self}: #{response.message}"
      end
    end
  end
end
