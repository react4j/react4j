package com.example.arez;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ComponentWithNoAutoActionCallback
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback( initCallbackContext = Feature.DISABLE )
  void handleFoo()
  {
  }
}
