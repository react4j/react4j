package com.example.nested;

import react4j.annotations.Callback;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

public class NestedCompleteComponent
{
  @ReactComponent( dagger = Feature.ENABLE, inject = Feature.ENABLE )
  static abstract class BasicReactComponent
    extends Component
  {
    @Prop
    abstract String getMyProp();

    @Callback
    void myEventHandler()
    {
    }

    @Override
    protected ReactNode render()
    {
      return null;
    }
  }
}
