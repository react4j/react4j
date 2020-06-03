package com.example.nested;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

public class NestedCompleteComponent
{
  @ReactComponent
  static abstract class BasicReactComponent
  {
    private final String _value;

    BasicReactComponent( final String value )
    {
      _value = value;
    }

    @Prop
    abstract String getMyProp();

    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
