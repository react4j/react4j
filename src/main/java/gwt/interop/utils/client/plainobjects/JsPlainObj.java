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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * This class represents a Plain Javascript Object i.e An object that
 * was created by calling new Object(). Typically in javascript they
 * are created as object literals using initializers e.g. var a = {prop: 10}
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name="Object")
public class JsPlainObj {
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
                  JsPropertyMap.of( jsPlainObj ).set( fieldName, ( (Integer) f).intValue() );
                else
                  JsPropertyMap.of( jsPlainObj ).set( fieldName, f );

                fieldName = null;
            }
        }

        return jsPlainObj;
    }
}
