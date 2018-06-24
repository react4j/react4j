package com.example.basic;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class GenericTypeComponent<T>
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
