package com.example.nested;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

public class NestedCompleteComponent
{
  @ReactComponent
  static abstract class BasicReactComponent
    extends Component
  {
    private final String _value;

    BasicReactComponent( final String value )
    {
      _value = value;
    }

    @Prop
    abstract String getMyProp();

    @Override
    protected ReactNode render()
    {
      return null;
    }
  }
}
