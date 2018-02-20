package com.example.callback;

import javax.annotation.Nullable;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class TypeParameterOnCallback
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback
  public <T> T handleFoo()
  {
    return null;
  }
}
