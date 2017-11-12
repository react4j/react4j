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
    'i' => 'HtmlProps',
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
package react4j.dom;

import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import react4j.core.ReactNode;
import react4j.core.util.JsUtil;
HEADER
  factories.values.sort.uniq.each do |prop_type|
    content += "import react4j.dom.proptypes.html.#{prop_type};\n"
  end

  content += <<HEADER

/**
 * DOM provides convenience wrappers around React.createElement for DOM components.
 */
@Generated( "GwtReact" )
public final class DOM
{
  private DOM()
  {
  }

  @Nonnull
  public static ReactNode text( @Nonnull final String content )
  {
    return ReactNode.of( content );
  }

HEADER
  factories.each_pair do |key, prop_type|
    content += <<HEADER

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props, @Nullable final ReactNode... child )
  {
    return ReactDOM.createElement( "#{key}", props, child );
  }

  @Nonnull
  public static ReactNode #{key}( @Nullable final ReactNode... child )
  {
    return ReactDOM.createElement( "#{key}", null, child );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props, @Nonnull final String content )
  {
    return ReactDOM.createElement( "#{key}", props, text( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props )
  {
    return ReactDOM.createElement( "#{key}", props );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final String content )
  {
    return ReactDOM.createElement( "#{key}", null, text( content ) );
  }

  @Nonnull
  public static ReactNode #{key}()
  {
    return ReactDOM.createElement( "#{key}", null );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props, @Nonnull final List<ReactNode> children )
  {
    return #{key}( props, Js.<ReactNode[]>uncheckedCast( JsUtil.asJsArray( children ) ) );
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
    file = "#{base_source_dir}/react4j/dom/DOM.java"
    mkdir_p File.dirname(file)
    IO.write(file, generate_factory)
  end
  dir = project.file(base_source_dir => [t.name])

  project.compile.from base_source_dir
  project.iml.main_generated_source_directories << dir
end
