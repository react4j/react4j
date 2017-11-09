package react4j.dom;
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

import elemental2.dom.Element;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.core.Procedure;
import react4j.core.ReactElement;
import react4j.core.ReactNode;
import react4j.dom.proptypes.html.HtmlGlobalFields;

@JsType( isNative = true, namespace = JsPackage.GLOBAL )
public class ReactDOM
{
  /**
   * This is a static class.
   */
  private ReactDOM()
  {
  }

  /**
   * <p>Render a ReactElement&lt;?, ?&gt; into the DOM in the supplied container and return a reference to
   * the component (or returns nothing for stateless components).</p>
   *
   * <p>If the ReactElement&lt;?, ?&gt; was previously rendered into container, this will perform an update
   * on it and only mutate the DOM as necessary to reflect the latest React component.</p>
   *
   * <p>If the optional callback is provided, it will be executed after the component is rendered
   * or updated.</p>
   *
   * @param element   the react element to render
   * @param container the DOM container to render into
   * @return a reference to the component or returns nothing for stateless components
   */
  @Nullable
  public static native ReactElement<?, ?> render( @Nonnull ReactElement<?, ?> element, @Nonnull Element container );

  @Nullable
  public static native ReactElement<?, ?> render( @Nonnull ReactElement<?, ?> element, @Nonnull Element container, Procedure onUpdate );

  /**
   * Remove a mounted React component from the DOM and clean up its event handlers and state. If
   * no component was mounted in the container, calling this function does nothing.
   *
   * @param container the DOM container containing the react component to unmount
   * @return <code>true</code> if the the component was unmounted
   */
  public static native boolean unmountComponentAtNode( @Nonnull Element container );

  /**
   * Create and return a new ReactElement of the given type.
   *
   * @param <P>  The type of the props. It should match the associated tag name.
   * @param type A HTML tag name (eg. 'div', 'span', etc)
   * @return the created DOMElement
   */
  @Nonnull
  @JsOverlay
  public static <P extends HtmlGlobalFields> DOMElement<P> createElement( @Nonnull final String type )
  {
    return createElement( type, null );
  }

  /**
   * Create and return a new ReactElement of the given type.
   *
   * @param <P>   The type of the props. It should match the associated tag name.
   * @param type  A HTML tag name (eg. 'div', 'span', etc)
   * @param props The props to pass to the element.
   * @return the created DOMElement
   */
  @Nonnull
  @JsMethod( namespace = "React" )
  public static native <P extends HtmlGlobalFields>
  DOMElement<P> createElement( @Nonnull String type, @Nullable P props );

  /**
   * Create and return a new ReactElement of the given type with specified children.
   *
   * @param <P>      The type of the props. It should match the associated tag name.
   * @param type     A HTML tag name (eg. 'div', 'span', etc)
   * @param props    The props to pass to the element.
   * @param children The child elements.
   * @return the created DOMElement
   */
  @Nonnull
  @JsMethod( namespace = "React" )
  public static native <P extends HtmlGlobalFields>
  DOMElement<P> createElement( @Nonnull String type, @Nullable P props, @Nullable ReactNode... children );
}
