package react4j;

import elemental2.core.JsArray;
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
    return new ProviderBuilder<>();
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
    return new ConsumerBuilder<>();
  }

  /**
   * A Builder for the Provider component.
   * A provider component is a react component that allows Consumers to subscribe to context changes.
   */
  public static final class ProviderBuilder<ST>
  {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();
    private final JsArray<ReactNode> _children = new JsArray<>();

    private ProviderBuilder()
    {
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
      _props.set( PropNames.KEY_PROP_NAME, Objects.requireNonNull( key ) );
      return this;
    }

    @Nonnull
    public final ProviderBuilder<ST> value( @Nullable final ST value )
    {
      _props.set( "value", value );
      return this;
    }

    @Nonnull
    public final ReactNode children( final ReactNode... children )
    {
      for ( final ReactNode child : children )
      {
        child( child );
      }
      return build();
    }

    @Nonnull
    public final ProviderBuilder<ST> child( @Nullable final ReactNode child )
    {
      if ( null != child )
      {
        _children.push( child );
      }
      return this;
    }

    @Nonnull
    public final ReactNode build()
    {
      return React.createElement( React.Provider, _props, _children.slice() );
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
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    private ConsumerBuilder()
    {
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
      _props.set( PropNames.KEY_PROP_NAME, Objects.requireNonNull( key ) );
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
      return React.createElement( React.Consumer, Js.uncheckedCast( _props ), Js.<ReactNode>cast( render ) );
    }
  }
}
