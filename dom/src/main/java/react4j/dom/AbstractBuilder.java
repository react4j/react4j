package react4j.dom;

import elemental2.core.Array;
import javaemul.internal.annotations.DoNotAutobox;
import javaemul.internal.annotations.ForceInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.ReactElement;
import react4j.core.ReactNode;
import react4j.core.RefConsumer;
import react4j.dom.proptypes.html.HtmlGlobalFields;

/**
 * Base class for all component and/or element builder instances.
 */
public abstract class AbstractBuilder<T extends AbstractBuilder>
{
  private final BaseProps _props = new BaseProps();
  private final Array<ReactNode> _children = new Array<>();

  @ForceInline
  public final T ref( @Nonnull final RefConsumer refCallback )
  {
    setProp( "ref", refCallback );
    return asT();
  }

  @ForceInline
  public final T key( @Nonnull final Object key )
  {
    setProp( "key", key );
    return asT();
  }

  @ForceInline
  public final <B extends AbstractBuilder> T child( @Nullable final B child )
  {
    return child( null == child ? null : child.build() );
  }

  @ForceInline
  public final T children( @Nullable final ReactElement<?, ?>[] child )
  {
    return child( ReactNode.of( child ) );
  }

  @ForceInline
  public final T child( @Nullable final ReactElement<?, ?> child )
  {
    return child( ReactNode.of( child ) );
  }

  @ForceInline
  public final T child( @Nullable final String child )
  {
    return child( ReactNode.of( child ) );
  }

  public final T child( @Nullable final ReactNode child )
  {
    _children.concat( child );
    return asT();
  }

  @Nonnull
  public abstract ReactNode build();

  @Nonnull
  protected final ReactNode build( @Nonnull final String input )
  {
    final HtmlGlobalFields props = Js.uncheckedCast( _props );
    final ReactNode[] children = Js.uncheckedCast( _children );
    return ReactNode.of( ReactDOM.createElement( input, props, Js.uncheckedCast( children ) ) );
  }

  @ForceInline
  protected final void setProp( @Nonnull final String key, @DoNotAutobox final Object value )
  {
    JsPropertyMap.of( _props ).set( key, value );
  }

  @ForceInline
  @Nonnull
  protected final T asT()
  {
    return Js.uncheckedCast( this );
  }
}
