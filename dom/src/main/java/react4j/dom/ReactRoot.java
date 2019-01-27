package react4j.dom;

import arez.SafeProcedure;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.ReactNode;

/**
 * A React root represents the top level DOM element that react binds to.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface ReactRoot
{
  @JsOverlay
  default void render( @Nonnull ReactNode children )
  {
    render( children, null );
  }

  void render( @Nonnull ReactNode children, @Nullable SafeProcedure callback );

  @JsOverlay
  default void unmount()
  {
    unmount( null );
  }

  void unmount( @Nullable SafeProcedure callback );
}
