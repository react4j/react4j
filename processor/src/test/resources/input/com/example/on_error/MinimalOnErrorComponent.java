package com.example.on_error;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MinimalOnErrorComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @OnError
  void onError()
  {
  }
}
