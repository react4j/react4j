package com.example.nested;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

public class NestedCompleteComponent
{
  @ReactComponent( inject = Feature.ENABLE )
  static abstract class BasicReactComponent
    extends Component
  {
    @Prop
    abstract String getMyProp();

    @Override
    protected ReactNode render()
    {
      return null;
    }
  }
}
