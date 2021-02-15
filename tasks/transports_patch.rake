# Patch qualifies File symbol as ::File and required to support later rubies

module URI
  class Generic
    def download(target, options = nil)
      case target
      when Rake::Task
        download target.name, options
      when String
        # If download breaks we end up with a partial file which is
        # worse than not having a file at all, so download to temporary
        # file and then move over.
        modified = ::File.stat(target).mtime if ::File.exist?(target)
        temp = Tempfile.new(::File.basename(target))
        temp.binmode
        written = false
        read({ :progress => verbose }.merge(options || {}).merge(:modified => modified)) { |chunk| written = true; temp.write chunk }
        temp.close
        mkpath ::File.dirname(target)
        # Only attempt to override file if it was actually written to, i.e. "HTTP Not Modified" was not returned.
        mv temp.path, target if written
      when ::File
        read({ :progress => verbose }.merge(options || {}).merge(:modified => target.mtime)) { |chunk| target.write chunk }
        target.flush
      else
        raise ArgumentError, 'Expecting a target that is either a file name (string, task) or object that responds to write (file, pipe).' unless target.respond_to?(:write)
        read({ :progress => verbose }.merge(options || {})) { |chunk| target.write chunk }
        target.flush
      end
    end

    def upload(source, options = nil)
      source = source.name if Rake::Task === source
      options ||= {}
      if String === source
        raise NotFoundError, 'No source file/directory to upload.' unless ::File.exist?(source)
        if ::File.directory?(source)
          Dir.glob("#{source}/**/*").reject { |file| ::File.directory?(file) }.each do |file|
            uri = self + (::File.join(self.path, file.sub(source, '')))
            uri.upload file, { :digests => [] }.merge(options)
          end
        else
          ::File.open(source, 'rb') { |input| upload input, options }
        end
      elsif source.respond_to?(:read)
        digests = (options[:digests] || [:md5, :sha1]).
          inject({}) { |hash, name| hash[name] = name.to_s == 'sha512' ? Digest::SHA2.new(512) : Digest.const_get(name.to_s.upcase).new; hash }
        size = source.stat.size rescue nil
        write (options).merge(:progress => verbose && size, :size => size) do |bytes|
          source.read(bytes).tap do |chunk|
            digests.values.each { |digest| digest << chunk } if chunk
          end
        end
        digests.each do |key, digest|
          self.merge("#{self.path}.#{key}").write digest.hexdigest,
                                                  (options).merge(:progress => false)
        end
      else
        raise ArgumentError, 'Expecting source to be a file name (string, task) or any object that responds to read (file, pipe).'
      end
    end

    protected

    def proxy_uri
      proxy = ENV["#{scheme.upcase}_PROXY"]
      proxy = URI.parse(proxy) if String === proxy
      excludes = ENV['NO_PROXY'].to_s.split(/\s*,\s*/).compact
      excludes = excludes.map { |exclude| exclude =~ /:\d+$/ ? exclude : "#{exclude}:*" }
      return proxy unless excludes.any? { |exclude| ::File.fnmatch(exclude, "#{host}:#{port}") }
    end
  end

  class SFTP
    protected

    def write_internal(options, &block)
      #:nodoc:
      # SSH options are based on the username/password from the URI.
      ssh_options = { :port => port, :password => password }.merge(options[:ssh_options] || {})
      ssh_options[:password] ||= SFTP.passwords[host]
      begin
        trace "Connecting to #{host}"
        Net::SFTP.start(host, user, ssh_options) do |sftp|
          SFTP.passwords[host] = ssh_options[:password]
          trace 'Connected'

          # To create a path, we need to create all its parent. We use realpath to determine if
          # the path already exists, otherwise mkdir fails.
          trace "Creating path #{path}"
          ::File.dirname(path).split('/').reject(&:empty?).inject('/') do |base, part|
            combined = base + part
            sftp.close(sftp.opendir!(combined)) rescue sftp.mkdir! combined, {}
            "#{combined}/"
          end

          with_progress_bar options[:progress] && options[:size], path.split('/').last, options[:size] || 0 do |progress|
            trace "Uploading to #{path}"
            sftp.file.open(path, 'w') do |file|
              while chunk = yield(RW_CHUNK_SIZE)
                file.write chunk
                progress << chunk
              end
              sftp.setstat(path, :permissions => options[:permissions]) if options[:permissions]
            end
          end
        end
      rescue Net::SSH::AuthenticationFailed => ex
        # Only if running with console, prompt for password.
        if !ssh_options[:password] && $stdout.isatty
          password = Buildr::Console.ask_password("Password for #{host}:") { |q| q.echo = '*' }
          ssh_options[:password] = password
          retry
        end
        raise
      end
    end
  end

  class FILE
    def real_path #:nodoc:
      real_path = Buildr::Util.win_os? && path =~ /^\/[a-zA-Z]:\// ? path[1..-1] : path
      URI.decode(real_path)
    end

    def upload(source, options = nil)
      super
      if ::File === source then
        ::File.chmod(source.stat.mode, real_path)
      end
    end

    def read(options = nil, &block)
      options ||= {}
      raise ArgumentError, 'Either you\'re attempting to read a file from another host (which we don\'t support), or you used two slashes by mistake, where you should have file:///<path>.' if host

      path = real_path
      # TODO: complain about clunky URLs
      raise NotFoundError, "Looking for #{self} and can't find it." unless ::File.exists?(path)
      raise NotFoundError, "Looking for the file #{self}, and it happens to be a directory." if ::File.directory?(path)
      ::File.open path, 'rb' do |input|
        with_progress_bar options[:progress], path.split('/').last, input.stat.size do |progress|
          block ? block.call(input.read) : input.read
        end
      end
    end

    protected

    def write_internal(options, &block)
      #:nodoc:
      raise ArgumentError, 'Either you\'re attempting to write a file to another host (which we don\'t support), or you used two slashes by mistake, where you should have file:///<path>.' if host
      temp = Tempfile.new(::File.basename(path))
      temp.binmode
      with_progress_bar options[:progress] && options[:size], path.split('/').last, options[:size] || 0 do |progress|
        while chunk = yield(RW_CHUNK_SIZE)
          temp.write chunk
          progress << chunk
        end
      end
      temp.close
      mkpath ::File.dirname(real_path)
      mv temp.path, real_path
      real_path
    end
  end
end
