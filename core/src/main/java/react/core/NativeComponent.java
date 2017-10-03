package react.core;
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
 * You can subclass {@link NativeComponent} to define a stateful, ES6-style React component.
 *
 * In addition to the methods defined in this class, you can also define the following lifecycle methods as needed:
 *
 * <pre>
 * void componentWillMount()
 * void componentDidMount()
 * void componentWillReceiveProps(P nextProps)
 * boolean shouldComponentUpdate(P nextProps, S nextState)
 * void componentWillUpdate(P nextProps, S nextState)
 * void componentDidUpdate(P prevProps, S prevState)
 * void componentWillUnmount()
 * </pre>
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

  @JsProperty
  private JsPropertyMap<Object> refs;

  protected NativeComponent( @Nonnull final P props )
  {
  }

  /**
   * Performs a shallow merge of nextState into current state. This is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param state Object Literal (containing zero or more keys to update)
   */
  public native void setState( S state );

  /**
   * Performs a shallow merge of nextState into current state. This is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param state    Object Literal (containing zero or more keys to update)
   * @param callback callback function that will be executed once setState is completed and
   *                 the component is re-rendered.
   */
  public native void setState( S state, Procedure callback );

  /**
   * Performs a shallow merge of nextState into current state. This is the primary method
   * you use to trigger UI updates from event handlers and server request callbacks.
   *
   * <p>It's also possible to pass a function with the signature function(state, props).
   * This can be useful in some cases when you want to enqueue an atomic update that
   * consults the previous value of state+props before setting any values</p>
   *
   * @param callback callback function that will be executed once setState is completed and
   *                 the component is re-rendered.
   */
  public native void setState( @Nonnull Component.SetStateCallback<P, S> callback );

  /**
   * <p>By default, when your component's state or props change, your component will re-render.
   * However, if these change implicitly (eg: data deep within an object changes without
   * changing the object itself) or if your render() method depends on some other data, you can
   * tell React that it needs to re-run render() by calling forceUpdate().</p>
   *
   * <p>Calling forceUpdate() will cause render() to be called on the component, skipping
   * shouldComponentUpdate(). This will trigger the normal lifecycle methods for child
   * components, including the shouldComponentUpdate() method of each child. React will still
   * only update the DOM if the markup changes.</p>
   *
   * <p>Normally you should try to avoid all uses of forceUpdate() and only read from this.props
   * and this.state in render(). This makes your component "pure" and your application much
   * simpler and more efficient.</p>
   *
   * @param callBack callback function that will be executed once the component has been updated
   */
  protected native void forceUpdate( Procedure callBack );

  public native void forceUpdate();

  /**
   * The render() method is required.
   *
   * <p>When called, it should examine props and state and return a single child element.
   * This child element can be either a virtual representation of a native DOM component
   * (such as React.DOM.div()) or another composite component that you've defined
   * yourself.</p>
   *
   * <p>You can also return null to indicate that you don't want anything rendered.
   * Behind the scenes, React renders a &lt;noscript&gt; tag to work with our current diffing
   * algorithm.</p>
   *
   * <p>The render() function should be pure, meaning that it does not modify component
   * state, it returns the same result each time it's invoked, and it does not read from
   * or write to the DOM or otherwise interact with the browser (e.g., by using setTimeout).
   * If you need to interact with the browser, perform your work in componentDidMount() or
   * the other lifecycle methods instead. Keeping render() pure makes components easier to
   * think about.</p>
   *
   * @return A single {@link ReactElement}
   */
  @JsMethod
  @Nullable
  protected abstract ReactElement<?, ?> render();

  @JsOverlay
  @Nullable
  public final S state()
  {
    return state;
  }

  @JsOverlay
  @Nullable
  public final P props()
  {
    return props;
  }

  @JsOverlay
  public final void setProps( @Nonnull final P props )
  {
    this.props = props;
  }

  @JsOverlay
  public final void setInitialState( @Nonnull final S state )
  {
    this.state = Objects.requireNonNull( state );
  }

  @JsOverlay
  public final JsPropertyMap<Object> refs()
  {
    return refs;
  }
}
