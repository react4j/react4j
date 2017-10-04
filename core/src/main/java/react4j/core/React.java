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

import elemental2.core.Array;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;

@JsType( isNative = true, namespace = JsPackage.GLOBAL )
public class React
{
  /**
   * This is a static class.
   */
  private React()
  {
  }

  /**
   * <p>Create and return a new {@link ReactElement} for the provided React component class.</p>
   *
   * <p>Note that calling this method will have undefined results in the following circumstances:</p>
   *
   * <p>1) when invoked for Java classes that are not exported to JavaScript via {@link JsType}</p>
   * <p>2) with Java classes exported to JavaScript under a different name (i.e. when the class was renamed using the 'namespace' and/or 'name' attributes
   * of the {@link JsType} annotation)</p>
   * <p>3) When class data is disabled when building</p>
   *
   * @param type  the {@link Class} object of a React component extending {@link NativeComponent}
   * @return a {@link ReactElement}
   */

  public static native <P extends BaseProps, S extends BaseState, T extends NativeComponent<P, S>> ReactElement<P, T>
  createElement( @Nonnull JsConstructorFn<T> type );

  /**
   * <p>Create and return a new {@link ReactElement} for the provided React component class.</p>
   *
   * <p>Note that calling this method will have undefined results in the following circumstances:</p>
   *
   * <p>1) when invoked for Java classes that are not exported to JavaScript via {@link JsType}</p>
   * <p>2) with Java classes exported to JavaScript under a different name (i.e. when the class was renamed using the 'namespace' and/or 'name' attributes
   * of the {@link JsType} annotation)</p>
   * <p>3) When class data is disabled when building</p>
   *
   * @param type  the {@link Class} object of a React component extending {@link NativeComponent}
   * @param props the props to pass to the element
   * @return a {@link ReactElement}
   */
  public static native <P extends BaseProps, S extends BaseState, T extends NativeComponent<P, S>> ReactElement<P, T>
  createElement( @Nonnull JsConstructorFn<T> type, @Nonnull P props );

  public static native <P extends BaseProps, S extends BaseState, T extends NativeComponent<P, S>> ReactElement<P, T>
  createElement( @Nonnull JsConstructorFn<T> type, @Nonnull P props, @Nonnull String value );

  public static native <P extends BaseProps, S extends BaseState, T extends NativeComponent<P, S>> ReactElement<P, T>
  createElement( @Nonnull JsConstructorFn<T> type, @Nonnull P props, @Nonnull ReactElement<?, ?>... child );

  /**
   * <p>Clone and return a new ReactElement using element as the starting point. The resulting
   * element will have the original element's props with the new props merged in shallowly.
   * New children will replace existing children. Unlike React.addons.cloneWithProps, key and
   * ref from the original element will be preserved. There is no special behavior for merging
   * any props (unlike cloneWithProps). See the v0.13 RC2 blog post for additional details.</p>
   *
   * @param element the element to clone
   * @param props   the props to merge
   * @return the cloned element
   */
  public static native <P extends BaseProps, T> ReactElement<P, T> cloneElement( ReactElement<P, T> element,
                                                                                 P props );

  public static native boolean isValidElement( Object object );

  /**
   * React.Children provides utilities for dealing with the this.props.children opaque data
   * structure.
   */
  @JsType( isNative = true )
  public static class Children
  {
    /**
     * This is a static class.
     */
    private Children()
    {
    }

    /**
     * Invoke fn on every immediate child contained within children. If children is a nested
     * object or array it will be traversed: fn will never be passed the container objects.
     * If children is null returns null rather than an array.
     *
     * @param children The opaque children structure to iterate over
     * @param Fn       The function to execute
     * @return An Array of child ReactElements
     */
    public static native Array<ReactElement<?, ?>> map( ReactElementChildren children,
                                                        ChildrenMapFn Fn ); // Object thisArg);

    /**
     * Like React.Children.map() but does not return an array.
     *
     * @param children The opaque children structure to iterate over
     * @param fn       The function to execute
     */
    public static native void forEach( ReactElementChildren children, ChildrenForEachFn fn ); // [, object thisArg]);

    /**
     * Return the total number of components in children, equal to the number of times that a
     * callback passed to map or forEach would be invoked.
     *
     * @param children The opaque children structure to count
     * @return the number of child components
     */
    public static native int count( ReactElementChildren children );

    /**
     * Return the only child in children. Throws otherwise.
     *
     * @param children The opaque children structure to access
     * @return the first and only child. Throws an exception if more than one exis
     */
    public static native ReactElement<?, ?> only( ReactElementChildren children );

    /**
     * Return the children opaque data structure as a flat array with keys assigned to
     * each child. Useful if you want to manipulate collections of children in your render
     * methods, especially if you want to reorder or slice this.props.children before passing
     * it down.
     *
     * @param children The opaque children structure to iterate over
     * @return a {@link Array} of {@link ReactElement}
     */
    public static native Array<ReactElement<?, ?>> toArray( ReactElementChildren children );
  }
}
