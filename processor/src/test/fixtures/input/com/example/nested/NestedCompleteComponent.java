package com.example.nested;

import javax.annotation.Nullable;
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

    @Nullable
    @Input
    abstract String getMyProp();

    @Nullable
    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
