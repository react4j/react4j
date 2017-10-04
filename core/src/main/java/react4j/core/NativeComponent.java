package react4j.core;
/* The MIT License (MIT)

Copyright (c) 2016 GWT React

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * The react native component.
 *
 * @param <P> the type of props this component expects
 * @param <S> the type of state this component maintains
 */
@JsType( isNative = true, namespace = "React", name = "Component" )
public abstract class NativeComponent<P extends BaseProps, S extends BaseState>
{
  @JsProperty
  private S state;

  @SuppressWarnings( "unused" )
  @JsProperty
  private P props;

  @SuppressWarnings( "unused" )
  @JsProperty
  private JsPropertyMap<Object> refs;

  protected NativeComponent( @Nonnull final P props )
  {
  }

  native void setState( S state );

  native void setState( @Nonnull Component.SetStateCallback<P, S> callback );

  native void forceUpdate();

  @JsMethod
  @Nullable
  protected abstract ReactElement<?, ?> render();

  @JsOverlay
  @Nullable
  final S state()
  {
    return state;
  }

  @JsOverlay
  @Nullable
  final P props()
  {
    return props;
  }

  @JsOverlay
  final void setInitialState( @Nonnull final S state )
  {
    this.state = Objects.requireNonNull( state );
  }

  @JsOverlay
  final JsPropertyMap<Object> refs()
  {
    return refs;
  }
}
