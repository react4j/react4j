package gwt.interop.utils.client.plainobjects;
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

import gwt.interop.utils.client.JSON;
import gwt.interop.utils.shared.JsHelper;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This class represents a Plain Javascript Object i.e An object that
 * was created by calling new Object(). Typically in javascript they
 * are created as object literals using initializers e.g. var a = {prop: 10}
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name="Object")
public class JsPlainObj {

    /**
     * Initialize a plain object with the specified property values. For example,
     * $jsPlainObj("a",1, "b","somevalue) is eqivalent to the following javascript
     *  { a: 1, b: "somevalue" }
     *
     * @param fieldValues a set of 1 or more (field name, value) pairs
     * @return The initialzed plain object
     */
    @JsOverlay
    public static JsPlainObj $jsPlainObj(Object ...fieldValues) {
        return $(new JsPlainObj(), fieldValues);
    }

    /**
     * Initialize a specified plain object with the specified property values. For example,
     * $(new MyJsPlainObj(), "a",1, "b","somevalue) is eqivalent to the following javascript
     *  { a: 1, b: "somevalue" }
     *
     *  @param <O> The type of plain object
     *  @param jsPlainObj The object to initialize
     *  @param fieldValues a set of 1 or more (field name, value) pairs
     *  @return The initialzed plain object
     */
    @JsOverlay
    public static <O> O $(O jsPlainObj, Object ...fieldValues) {
        String fieldName = null;

        for(Object f : fieldValues) {
            if (fieldName == null)
                fieldName = (String)f;
            else {
                if (f instanceof Integer)
                    JsHelper.setObjectProperty(jsPlainObj, fieldName, ((Integer) f).intValue());
                else
                    JsHelper.setObjectProperty(jsPlainObj, fieldName, f);

                fieldName = null;
            }
        }

        return jsPlainObj;
    }

    /**
     * Return an int parameter from this plain object. NOTE this is method is NOT type safe. If if you access
     * a property that isn't an int you will get an undefined result. Also if you access a property
     * that does not exist you will get an undefined error
     *
     * @param prop  The property to access
     * @return      The integer property value
     */
    @JsOverlay
    final public int getInt(String prop) {
        return JsHelper.getObjectIntProperty(this, prop);
    }

    /**
     * Return a double parameter from this plain object. NOTE this method is NOT type safe. If you access
     * a property that isn't a double you will get an undefined result. Also if you access a property
     * that does not exist you will get an undefined error
     *
     * @param prop  The property to access
     * @return      The double property value
     */
    @JsOverlay
    final public double getDbl(String prop) {
        return JsHelper.getObjectProperty(this, prop);
    }

    /**
     * Return an boolean parameter from this plain object. NOTE this method is NOT type safe. If you access
     * a property that isn't a boolean you will get an undefined result. Also if you access a property
     * that does not exist you will get an undefined error
     *
     * @param prop  The property to access
     * @return      The boolean property value
     */
    @JsOverlay
    final public boolean getBool(String prop) {
        return JsHelper.getObjectProperty(this, prop);
    }

    /**
     * Return an String parameter from this plain object. NOTE this method is NOT type safe. If you access
     * a property that isn't an String you will get an undefined result. Also if you access a property
     * that does not exist you will get an undefined error
     *
     * @param prop  The property to access
     * @return      The String property value
     */
    @JsOverlay
    final public String getStr(String prop) {
        return JsHelper.getObjectProperty(this, prop);
    }

    /**
     * Return an Object parameter from this plain object. NOTE this method is NOT type safe. If
     * you access a property that isn't an Object you will get an undefined result. Also if you
     * access a property that does not exist you will get an undefined error
     *
     * @param prop  The property to access
     * @param <O>   The Object property value
     * @return      The Object property value
     */
    @JsOverlay
    final public <O> O getObj(String prop) {
        return JsHelper.getObjectProperty(this, prop);
    }

    /**
     * Set the specified property on this plain object. The property will be added if it doesn't
     * currently exist
     *
     * @param prop  The property to set
     * @param v     The value to set the property to
     */
    @JsOverlay
    final public void set(String prop, int v) {
        JsHelper.setObjectProperty(this, prop, v);
    }

    /**
     * Set the specified property on this plain object. The property will be added if it doesn't
     * currently exist
     *
     * @param prop  The property to set
     * @param v     The value to set the property to
     */
    @JsOverlay
    final public void set(String prop, double v) {
        JsHelper.setObjectProperty(this, prop, v);
    }

    /**
     * Set the specified property on this plain object. The property will be added if it doesn't
     * currently exist
     *
     * @param prop  The property to set
     * @param v     The value to set the property to
     */
    @JsOverlay
    final public void set(String prop, boolean v) {
        JsHelper.setObjectProperty(this, prop, v);
    }

    /**
     * Set the specified property on this plain object. The property will be added if it doesn't
     * currently exist
     *
     * @param prop  The property to set
     * @param v     The value to set the property to
     */
    @JsOverlay
    final public void set(String prop, String v) {
        JsHelper.setObjectProperty(this, prop, v);
    }

    /**
     * Set the specified property on this plain object. The property will be added if it doesn't
     * currently exist
     *
     * @param prop  The property to set
     * @param <V> The type of value to set
     * @param v The value to set the property to
     */
    @JsOverlay
    final public <V> void set(String prop, V v) {
        JsHelper.setObjectProperty(this, prop, v);
    }

    @JsOverlay
    final public String toJSONString() {
        return JSON.stringify(this);
    }

    /**
     * Return a new plain object that is the result of merging this plain object with the supplied
     * plain object. Any property in this object that has the same name in toMerge will be replaced
     * with toMerge's value e.g {a:1, b:1}.merge({b:2, c:3}) would result in {a:1, b:2, c:3}
     *
     * @param toMerge The object to merge
     * @param <R> The type of object returned from the merge operation
     * @param <O> The type of plain object to merge
     * @return The merged JsPlainObj
     */
    @JsOverlay
    final public <R extends JsPlainObj, O extends JsPlainObj> R merge(O toMerge) {
        return JsHelper.merge(this, toMerge);
    }

    /**
     * Return a new plain object that contains all the properties of this object except the ones
     * defined in the exclude list e.g {a:1, b:2, c:3}.exclude("a","c") would result in {b:2}
     *
     * @param <R> The type of object returned from the except operation
     * @param exclude The properties to exclude
     * @return The new JsPlainObj
     */
    @JsOverlay
    final public <R extends JsPlainObj> R except(String ...exclude) {
        return JsHelper.except(this, exclude);
    }
}
