def dom_factory_types
  {
    'a' => 'AnchorProps',
    'article' => 'HtmlProps',
    'audio' => 'AudioProps',
    'br' => 'HtmlProps',
    'button' => 'BtnProps',
    'canvas' => 'HtmlProps',
    'caption' => 'HtmlProps',
    'col' => 'ColProps',
    'colgroup' => 'HtmlProps',
    'div' => 'HtmlProps',
    'footer' => 'HtmlProps',
    'form' => 'FormProps',
    'header' => 'HtmlProps',
    'h1' => 'HtmlProps',
    'h2' => 'HtmlProps',
    'h3' => 'HtmlProps',
    'h4' => 'HtmlProps',
    'h5' => 'HtmlProps',
    'h6' => 'HtmlProps',
    'iframe' => 'IFrameProps',
    'img' => 'ImgProps',
    'input' => 'InputProps',
    'label' => 'LabelProps',
    'li' => 'HtmlProps',
    'ol' => 'HtmlProps',
    'option' => 'OptionProps',
    'optgroup' => 'OptGroupProps',
    'p' => 'HtmlProps',
    'span' => 'HtmlProps',
    'select' => 'SelectProps',
    'section' => 'HtmlProps',
    'strong' => 'HtmlProps',
    'source' => 'SourceProps',
    'table' => 'HtmlProps',
    'textarea' => 'TextAreaProps',
    'td' => 'TdProps',
    'th' => 'ThProps',
    'tr' => 'HtmlProps',
    'ul' => 'HtmlProps'
  }
end

def generate_factory
  factories = dom_factory_types
  content = <<HEADER
package react.dom;

import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import react.common.JsUtil;
import react.core.ReactElement;
HEADER
  factories.values.sort.uniq.each do |prop_type|
    content += "import react.dom.proptypes.html.#{prop_type};\n"
  end

  content += <<HEADER

/**
 * DOM provides convenience wrappers around React.createElement for DOM components.
 */
@Generated( "GwtReact" )
public class DOM
{
  /**
   * This is a static class.
   */
  private DOM()
  {
  }

  @Nonnull
  public static ReactElement<?, ?> text( @Nonnull final String content )
  {
    return Js.uncheckedCast( content );
  }

HEADER
  factories.each_pair do |key, prop_type|
    content += <<HEADER

  @Nonnull
  public static DOMElement<#{prop_type}> #{key}( @Nonnull final #{prop_type} props, @Nullable final ReactElement<?, ?>... child )
  {
    return ReactDOM.createElement( "#{key}", props, child );
  }

  @Nonnull
  public static DOMElement<#{prop_type}> #{key}( @Nullable final ReactElement<?, ?>... child )
  {
    return ReactDOM.createElement( "#{key}", null, child );
  }

  @Nonnull
  public static DOMElement<#{prop_type}> #{key}( @Nonnull final #{prop_type} props, @Nonnull final String content )
  {
    return ReactDOM.createElement( "#{key}", props, content );
  }

  @Nonnull
  public static DOMElement<#{prop_type}> #{key}( @Nonnull final String content )
  {
    return ReactDOM.createElement( "#{key}", null, content );
  }

  @Nonnull
  public static DOMElement<#{prop_type}> #{key}()
  {
    return ReactDOM.createElement( "#{key}", null );
  }

  @Nonnull
  public static DOMElement<#{prop_type}> #{key}( @Nonnull final #{prop_type} props, @Nonnull final List<ReactElement<?, ?>> children )
  {
    return #{key}( props, Js.<ReactElement<?, ?>[]>uncheckedCast( JsUtil.asJsArray( children ) ) );
  }
HEADER
  end

  content += <<FOOTER
}
FOOTER

  content
end

def generate_factory_source(project)

  base_source_dir = project._(:generated, :reactgen, :main, :java)
  desc 'Generate the dom factories'
  t = project.task('reactgen') do
    file = "#{base_source_dir}/react/dom/DOM.java"
    mkdir_p File.dirname(file)
    IO.write(file, generate_factory)
  end
  dir = project.file(base_source_dir => [t.name])

  project.compile.from base_source_dir
  project.iml.main_generated_source_directories << dir
end
