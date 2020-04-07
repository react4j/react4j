package com.example.inject.autofragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

// Class exists so that the synthesizing processor has an annotation to "process"
@Target( ElementType.TYPE )
public @interface MyFramework
{
}
