package react4j.core;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * The react native component.
 *
 * @param <P> the type of props this component expects
 * @param <S> the type of state this component maintains
 */
@JsType( isNative = true, namespace = "React", name = "Component" )
public abstract class NativeComponent<P extends BaseProps, S extends BaseState, C extends BaseContext>
{
  @SuppressWarnings( "unused" )
  @JsProperty
  private P props;
  @JsProperty
  private S state;
  @JsProperty
  private C context;

  NativeComponent( @SuppressWarnings( "unused" ) @Nullable final P props,
                   @SuppressWarnings( "unused" ) @Nullable final C context )
  {
  }

  @JsMethod
  @Nullable
  public abstract ReactNode render();

  @JsOverlay
  @Nullable
  final P props()
  {
    return props;
  }

  @JsOverlay
  @Nullable
  final S state()
  {
    return state;
  }

  @JsOverlay
  @Nullable
  final C context()
  {
    return context;
  }

  final native void setState( @Nonnull Component.SetStateCallback<P, S> callback );

  @JsOverlay
  final void setInitialState( @Nonnull final S state )
  {
    this.state = Objects.requireNonNull( state );
  }

  final native void forceUpdate();
}
