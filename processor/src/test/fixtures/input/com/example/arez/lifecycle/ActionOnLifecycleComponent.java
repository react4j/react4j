package com.example.arez.lifecycle;

import javax.annotation.Nullable;
import javax.xml.ws.Action;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class ActionOnLifecycleComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Action
  @PostMount
  protected void postMount()
  {
  }
}
