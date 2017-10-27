module Jekyll
  #
  # A simple tag for generating url for api documentation.
  #
  # {% api_url ArezContext %}
  # {% api_url Observable reportChanged() %}
  #
  class ApiUrl < Liquid::Tag
    def initialize(tag_name, markup, tokens)
      markup = markup.strip
      parts = markup.split(' ')
      if parts.size != 1 && parts.size != 2
        raise "Unexpected parameter to api_url #{markup}. Expected it to be of the form 'classname' or 'classname element'"
      end
      @classname = parts[0]
      @element = parts[1] if parts.size == 2

      super
    end

    def render(context)
      classname = "react4j.#{@classname}"
      element = (@element || '').gsub('(','-').gsub(')','-')
      element = "##{element}" if element.size > 0
      return "#{context['site']['baseurl']}/api/#{classname.gsub('.','/')}.html#{element}"
    end
  end
end

Liquid::Template.register_tag('api_url', Jekyll::ApiUrl)
