module Jekyll
  #
  # A simple tag for generating url for api documentation.
  #
  # {% api_url ArezContext %}
  #
  class ApiUrl < Liquid::Tag
    def initialize(tag_name, markup, tokens)
      markup = markup.strip
      @classname = markup

      super
    end

    def render(context)
      classname = "react4j.#{@classname}"
      return "#{context['site']['baseurl']}/api/#{classname.gsub('.','/')}.html"
    end
  end
end

Liquid::Template.register_tag('api_url', Jekyll::ApiUrl)
