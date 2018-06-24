package com.example.callback;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class EnabledCallback
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback( initCallbackContext = Feature.ENABLE )
  void handleFoo()
  {
  }
}
