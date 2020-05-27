package react4j;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Context is designed to share data that can be considered "global" for a tree of React components.
 * In a typical React application, data is passed top-down (parent to child) via props, but this can
 * be cumbersome for certain types of props (e.g. locale preference, UI theme) that are required by
 * \many components within an application. Context provides a way to share values like this between
 * components without having to explicitly pass a prop through every level of the tree.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class Context<T>
{
  /**
   * Create a builder for the Provider component.
   *
   * @return a builder for the Provider component.
   */
  @JsOverlay
  @Nonnull
  public final ProviderBuilder<T> provider()
  {
    return new ProviderBuilder<>( this );
  }

  /**
   * Create a provider component that provides specified value to specified children.
   *
   * @param value    the value to provider to children.
   * @param children the child elements.
   * @return the element.
   */
  @JsOverlay
  @Nonnull
  public final ReactNode provide( @Nullable final T value, final ReactNode... children )
  {
    return provider().value( value ).children( children );
  }

  /**
   * Create a builder for the Consumer component.
   *
   * @return a builder for the Consumer component.
   */
  @JsOverlay
  @Nonnull
  public final ConsumerBuilder<T> consumer()
  {
    return new ConsumerBuilder<>( this );
  }

  /**
   * A Builder for the Provider component.
   * A provider component is a react component that allows Consumers to subscribe to context changes.
   */
  public static final class ProviderBuilder<ST>
  {
    @Nonnull
    private final ReactElement _element;

    private ProviderBuilder( @Nonnull final Context<ST> context )
    {
      _element = ReactElement.createContextElement( Js.<JsPropertyMap<Object>>cast( context ).get( "Provider" ) );
    }

    /**
     * Specify the key for the component if required.
     *
     * @param key the key for the component.
     * @return the builder.
     */
    @Nonnull
    public final ProviderBuilder<ST> key( @Nonnull final String key )
    {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    @Nonnull
    public final ProviderBuilder<ST> value( @Nullable final ST value )
    {
      _element.props().set( "value", value );
      return this;
    }

    @Nonnull
    public final ReactNode children( final ReactNode... children )
    {
      _element.props().set( "children", children );
      return build();
    }

    @Nonnull
    public final ReactNode build()
    {
      _element.complete();
      return _element;
    }
  }

  /**
   * Interface used to type the render function prop.
   */
  @JsFunction
  @FunctionalInterface
  public interface ConsumerRenderFunction<T>
  {
    /**
     * Render the specified tree for context value.
     *
     * @param value the context value.
     * @return the rendered react node tree.
     */
    ReactNode render( T value );
  }

  /**
   * A Builder for the Consumer component.
   * A Consumer component is a react component that subscribes to context changes.
   */
  public static final class ConsumerBuilder<ST>
  {
    @Nonnull
    private final ReactElement _element;

    private ConsumerBuilder( @Nonnull final Context<ST> context )
    {
      _element = ReactElement.createContextElement( Js.<JsPropertyMap<Object>>cast( context ).get( "Consumer" ) );
    }

    /**
     * Specify the key for component if required.
     *
     * @param key the key for the component.
     * @return the builder.
     */
    @Nonnull
    public final ConsumerBuilder<ST> key( @Nonnull final String key )
    {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    /**
     * Specify the child render function.
     *
     * @param render the child render function.
     * @return the creates react node.
     */
    @Nonnull
    public final ReactNode render( @Nonnull final ConsumerRenderFunction<ST> render )
    {
      _element.props().set( "children", render );
      _element.complete();
      return _element;
    }
  }
}
