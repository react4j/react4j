package com.example.callback;

import react4j.annotations.Callback;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class EnabledCallback
  extends Component<BaseState>
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
