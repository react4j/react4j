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
    'tbody' => 'HtmlProps',
    'td' => 'TdProps',
    'th' => 'ThProps',
    'thead' => 'HtmlProps',
    'tr' => 'HtmlProps',
    'ul' => 'HtmlProps'
  }
end

def generate_factory
  factories = dom_factory_types
  content = <<HEADER
package react4j.dom;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
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

  // Arbitrarily large value as the time will be clamped to the expiration time of the whole tree
  private static final int DEFAULT_TIME_TO_FALLBACK = 1000000000;

  @Nonnull
  public static ReactNode text( @Nonnull final String content )
  {
    return ReactNode.of( content );
  }

  @Nonnull
  public static ReactNode fragment( @Nullable final String key, @Nonnull final ReactNode... children )
  {
    return ReactElement.createFragment( key, Objects.requireNonNull( children ) );
  }

  @Nonnull
  public static ReactNode fragment( @Nullable final String key, @Nonnull final List<? extends ReactNode> children )
  {
    return fragment( key, Objects.requireNonNull( children ).stream() );
  }

  @Nonnull
  public static ReactNode fragment( @Nullable final String key, @Nonnull final Stream<? extends ReactNode> children )
  {
    return ReactElement.createFragment( key, Objects.requireNonNull( children ).toArray( ReactNode[]::new ) );
  }

  @Nonnull
  public static ReactNode fragment( @Nonnull final ReactNode... children )
  {
    return ReactElement.createFragment( null, Objects.requireNonNull( children ) );
  }

  @Nonnull
  public static ReactNode fragment( @Nonnull final List<? extends ReactNode> children )
  {
    return fragment( null, Objects.requireNonNull( children ).stream() );
  }

  @Nonnull
  public static ReactNode fragment( @Nonnull final Stream<? extends ReactNode> children )
  {
    return ReactElement.createFragment( null, Objects.requireNonNull( children ).toArray( ReactNode[]::new ) );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final String key,
                                    @Nullable final ReactNode fallback,
                                    final int maxTimeToFallback,
                                    @Nonnull final ReactNode... children )
  {
    return ReactElement.createSuspense( key, fallback, maxTimeToFallback, Objects.requireNonNull( children ) );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final String key,
                                    @Nullable final ReactNode fallback,
                                    final int maxTimeToFallback,
                                    @Nonnull final List<? extends ReactNode> children )
  {
    return suspense( key, fallback, maxTimeToFallback, Objects.requireNonNull( children ).stream() );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final String key,
                                    @Nullable final ReactNode fallback,
                                    final int maxTimeToFallback,
                                    @Nonnull final Stream<? extends ReactNode> children )
  {
    return suspense( key, fallback, maxTimeToFallback, Objects.requireNonNull( children ).toArray( ReactNode[]::new ) );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final ReactNode fallback,
                                    final int maxTimeToFallback,
                                    @Nonnull final ReactNode... children )
  {
    return suspense( null, fallback, maxTimeToFallback, children );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final ReactNode fallback,
                                    final int maxTimeToFallback,
                                    @Nonnull final List<? extends ReactNode> children )
  {
    return suspense( null, fallback, maxTimeToFallback, children );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final ReactNode fallback,
                                    final int maxTimeToFallback,
                                    @Nonnull final Stream<? extends ReactNode> children )
  {
    return suspense( null, fallback, maxTimeToFallback, children );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final String key,
                                    @Nullable final ReactNode fallback,
                                    @Nonnull final ReactNode... children )
  {
    return suspense( key, fallback, DEFAULT_TIME_TO_FALLBACK, Objects.requireNonNull( children ) );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final String key,
                                    @Nullable final ReactNode fallback,
                                    @Nonnull final List<? extends ReactNode> children )
  {
    return suspense( key, fallback, DEFAULT_TIME_TO_FALLBACK, Objects.requireNonNull( children ).stream() );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final String key,
                                    @Nullable final ReactNode fallback,
                                    @Nonnull final Stream<? extends ReactNode> children )
  {
    return suspense( key, fallback, DEFAULT_TIME_TO_FALLBACK, Objects.requireNonNull( children ).toArray( ReactNode[]::new ) );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final ReactNode fallback, @Nonnull final ReactNode... children )
  {
    return suspense( null, fallback, DEFAULT_TIME_TO_FALLBACK, Objects.requireNonNull( children ) );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final ReactNode fallback,
                                    @Nonnull final List<? extends ReactNode> children )
  {
    return suspense( null, fallback, DEFAULT_TIME_TO_FALLBACK, Objects.requireNonNull( children ).stream() );
  }

  @Nonnull
  public static ReactNode suspense( @Nullable final ReactNode fallback,
                                    @Nonnull final Stream<? extends ReactNode> children )
  {
    return suspense( null, fallback, DEFAULT_TIME_TO_FALLBACK, Objects.requireNonNull( children ).toArray( ReactNode[]::new ) );
  }
HEADER
  factories.each_pair do |key, prop_type|
    content += <<HEADER

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props, @Nullable final ReactNode... children )
  {
    return React.createElement( "#{key}", props, children );
  }

  @Nonnull
  public static ReactNode #{key}( @Nullable final ReactNode... children )
  {
    return React.createElement( "#{key}", null, children );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props, @Nonnull final String content )
  {
    return React.createElement( "#{key}", props, text( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props )
  {
    return React.createElement( "#{key}", props );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final String content )
  {
    return #{key}( ReactNode.of( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( final byte content )
  {
    return #{key}( ReactNode.of( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( final short content )
  {
    return #{key}( ReactNode.of( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( final int content )
  {
    return #{key}( ReactNode.of( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( final long content )
  {
    return #{key}( ReactNode.of( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( final float content )
  {
    return #{key}( ReactNode.of( content ) );
  }

  @Nonnull
  public static ReactNode #{key}( final double content )
  {
    return #{key}( ReactNode.of( content ) );
  }

  @Nonnull
  public static ReactNode #{key}()
  {
    return React.createElement( "#{key}", null );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props, @Nonnull final List<? extends ReactNode> children )
  {
    return #{key}( props, children.stream() );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final List<? extends ReactNode> children )
  {
    return #{key}( children.stream() );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final #{prop_type} props, @Nonnull final Stream<? extends ReactNode> children )
  {
    return #{key}( props, children.toArray( ReactNode[]::new ) );
  }

  @Nonnull
  public static ReactNode #{key}( @Nonnull final Stream<? extends ReactNode> children )
  {
    return #{key}( children.toArray( ReactNode[]::new ) );
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

  project.compile.from dir
  project.iml.main_generated_source_directories << dir
end
