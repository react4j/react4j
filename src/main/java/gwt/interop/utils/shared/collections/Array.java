package gwt.interop.utils.shared.collections;
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

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * An interface to a Javascript array. The implementation may be different on the client
 * and server
 *
 * @param <T> The type of value this array holds
 * @author pstockley
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Array" )
public interface Array<T>
{
  @JsMethod( name = "concat" )
  Array<T> concatValue( T a );

  /**
   * Gets the object at a given index.
   *
   * @param index the index to be retrieved
   * @return the object at the given index, or <code>null</code> if none exists
   */
  @JsOverlay
  default T get( int index )
  {
    return JsArrayHelper.getArrayValue( this, index );
  }

  /**
   * Gets the length of the array.
   *
   * @return the array length
   */
  @JsProperty( name = "length" )
  int getLength();

  /**
   * Pushes the given value onto the end of the array.
   *
   * @param value The value to add to the end of the array
   */
  void push( T value );

  /**
   * Pushes the given value onto the end of the array.
   *
   * @param value The value to add to the end of the array
   */
  void push( T... value );

  @JsMethod( name = "forEach" )
  void forEachElem( ForEachFullFn<T> fn );

  /**
   * Creates a new array with all elements that pass the test implemented by the provided function
   * <p>
   * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/filter">filter spec</a>
   *
   * @param fn One of TestFn or TestFullFn
   * @return The new filtered array
   */
  Array<T> filter( TestFullFn<T> fn );

  /**
   * Creates a new array with the results of calling a provided function on every element in this array
   * <p>
   * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map">map spec</a>
   *
   * @param fn   One of MapFn or MapFullFn
   * @param <T2> The type of output from the map function
   * @return The new mapped array
   */
  <T2 extends Object> Array<T2> map( MapFullFn<T, T2> fn );

  /**
   * Applies a function against an accumulator and each value of the array (from left-to-right)
   * to reduce it to a single value
   * <p>
   * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/reduce">reduce spec</a>
   *
   * @param fn           One of ReduceFn, ReduceDoubleFn or ReduceFullFn
   * @param initialValue The initial accumulator value
   * @param <A>          The type of accumulator
   * @return The reduced accumulator value
   */
  <A extends Object> A reduce( ReduceFullFn<A, T> fn, A initialValue );

  /**
   * Tests whether some element in the array passes the run implemented by the provided function.
   * <p>
   * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/some">some spec</a>
   *
   * @param fn Function to run for each element, taking three arguments
   * @return true if the run function returns true for one of the elements
   */
  boolean some( TestFn<T> fn );

  boolean some( TestFullFn<T> fn );

  @JsFunction
  interface TestFn<T>
  {
    boolean test( T currentValue );
  }

  @JsFunction
  interface TestFullFn<T>
  {
    boolean test( T currentValue, int index, Array<T> theArray );
  }

  @JsFunction
  interface ForEachFullFn<T>
  {
    void exec( T currentValue, int index, Array<T> theArray );
  }

  @JsFunction
  interface MapFullFn<T, T2>
  {
    T2 doMap( T currentValue, int index, Array<T> theArray );
  }

  @JsFunction
  interface ReduceFullFn<A, T>
  {
    A doReduce( A PreviousAccumulator, T currentValue, int index, Array<T> theArray );
  }
}
