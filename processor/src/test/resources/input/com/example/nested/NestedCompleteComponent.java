package com.example.nested;

import react4j.annotations.EventHandler;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

public class NestedCompleteComponent
{
  @ReactComponent( dagger = Feature.ENABLE, inject = Feature.ENABLE )
  static abstract class BasicReactComponent
    extends Component<BaseProps, BaseState, BaseContext>
  {
    @Prop
    abstract String getMyProp();

    @EventHandler
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
