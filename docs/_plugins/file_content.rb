module Jekyll
  #
  # A simple tag for inserting code into documentation. If you want to insert based on regex start and end
  # you can add something like the following to your markdown:
  #
  # {% highlight java %}
  # {% file_content core:java:org/realityforge/arez/Unsupported.java "start_line=/public @interface/" "end_line=/\}/" %}
  # {% endhighlight %}
  #
  # Although there is also line based cutting such as
  #
  # {% highlight java %}
  # {% file_content core:java:org/realityforge/arez/Unsupported.java start_line=5 end_line=10 %}
  # {% endhighlight %}
  #
  class FileContent < Liquid::Tag
    def initialize(tag_name, markup, tokens)
      params = {}

      markup = markup.gsub(/(\s+)|("([^"\\]|\\.)*")|('([^'\\]|\\.)*')/m) {|m| $1 ? ' ' : $2 ? $2 : $3}.strip
      matches = indices(markup, / ([^ "'][^ ]+)| "(([^"\\]|\\.)*)"| '(([^'\\]|\\.)*)'/m)
      matches.insert(0, [0, 0, matches[0][0]])
      matches.collect {|m, s, e| markup[s, e]}.collect {|e| e.split('=')}.each do |p|
        if 1 == p.size
          params['file'] = p[0]
        else
          params[p[0]] = p[1...999999999].join('=')
        end
      end

      self.options = params

      p params

      raise 'Missing expected file parameter. Use tag syntax such as {% file_content core:java:org/realityforge/arez/ArezContext.java start_line=23 end_line=23 %}' unless self.file

      super
    end

    # noinspection RubyAssignmentExpressionInConditionalInspection,RubyUnusedLocalVariable
    def indices(string, e)
      match_start, result = -1, []
      last = -1
      while match_start = (string.index(e, last + 1))
        string_start = match_start + ($1 ? 1 : 2)
        length = ($1 || $2 || $3).length
        result << [match_start, string_start, length]
        last = string_start + length - 1 # + ($1 ? 0 : 1)
      end
      p result
      p string
      result
    end

    FILE_PATTERN = /^([a-zA-Z]+)\:([a-zA-Z]+)\:([^\:]+)$/

    attr_reader :file

    def file=(file)
      raise "File parameter '#{file}' not in expected format project:category:filename.yml such as core:java:org/realityforge/arez/ArezContext.java" unless file =~ FILE_PATTERN
      @file = file
    end

    attr_accessor :start_line
    attr_accessor :end_line

    def render(context)
      content = IO.read(self.derive_filename)
      if self.start_line || self.end_line
        lines = content.split("\n")

        first_line = calculate_first_line(lines)
        last_line = calculate_last_line(first_line, lines)

        if first_line < 0
          raise "Specified a negative first_line parameter when importing file #{self.derive_filename}"
        elsif last_line >= lines.size
          raise "Specified a last_line parameter that is after the end of file when importing file #{self.derive_filename}"
        elsif first_line > last_line
          raise "Specified a first_line that is after the last_line parameter when importing file #{self.derive_filename}"
        end

        lines[first_line..last_line].join("\n")
      else
        content
      end
    end

    def calculate_last_line(first_line, lines)
      last_line = lines.size - 1
      if self.end_line
        if self.end_line.to_i.to_s == self.end_line
          # if end_line is a number
          last_line = self.end_line.to_i
        elsif self.end_line =~ /^\/(.+)\/$/
          # if end_line is a regex
          regex = /#{$1}/
          last_line = nil
          lines[first_line..9999999999999].each_with_index do |line, index|
            if line =~ regex
              last_line = first_line + index
              break
            end
          end
          if last_line.nil?
            raise "Unable to locate line that matches regex #{regex} in file #{self.derive_filename} when attempting to identify start line"
          end
        else
          raise "The end_line parameter #{self.end_line} when importing file #{self.derive_filename} is not a number or a regex"
        end
      end
      last_line
    end

    def calculate_first_line(lines)
      first_line = 0
      if self.start_line
        if self.start_line.to_i.to_s == self.start_line
          # if start_line is a number
          first_line = self.start_line.to_i
        elsif self.start_line =~ /^\/(.+)\/$/
          # if start_line is a regex
          regex = /#{$1}/
          first_line = nil
          lines.each_with_index do |line, index|
            if line =~ regex
              first_line = index
              break
            end
          end
          if first_line.nil?
            raise "Unable to locate line that matches regex #{regex} in file #{self.derive_filename} when attempting to identify start line"
          end
        else
          raise "The start_line parameter #{self.start_line} when importing file #{self.derive_filename} is not a number or a regex"
        end
      end
      first_line
    end

    def derive_filename
      FILE_PATTERN =~ self.file

      project = $1
      category = $2
      file = $3

      workspace_dir = File.expand_path(File.dirname(__FILE__) + '/../../')

      filename = "#{workspace_dir}/#{project}/src/main/#{category}/#{file}"
      unless File.exist?(filename)
        raise "File does not exist.\nFile specifier: #{self.file}\nExpected filename:#{filename}"
      end
      filename
    end

    def options=(options)
      options.each_pair do |k, v|
        keys = k.to_s.split('.')
        target = self
        keys[0, keys.length - 1].each do |target_accessor_key|
          target = target.send target_accessor_key.to_sym
        end
        begin
          target.send "#{keys.last}=", v
        rescue NoMethodError
          raise "Attempted to configure property \"#{keys.last}\" on #{self.class} but property does not exist."
        end
      end
    end
  end
end

Liquid::Template.register_tag('file_content', Jekyll::FileContent)
