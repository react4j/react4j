package com.example.callback;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class TypeParameterOnComponentWithCallback<T>
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback
  void handleFoo()
  {
  }
}
