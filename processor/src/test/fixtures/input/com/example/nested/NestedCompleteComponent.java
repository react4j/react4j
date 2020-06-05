package com.example.nested;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

public class NestedCompleteComponent
{
  @View
  static abstract class BasicView
  {
    private final String _value;

    BasicView( final String value )
    {
      _value = value;
    }

    @Input
    abstract String getMyProp();

    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
