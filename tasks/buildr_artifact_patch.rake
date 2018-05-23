raise 'Patch already integrated into buildr code' unless Buildr::VERSION.to_s == '1.5.6'

class URI::HTTP
  private

  def write_internal(options, &block)
    options ||= {}
    connect do |http|
      trace "Uploading to #{path}"
      http.read_timeout = 500
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
