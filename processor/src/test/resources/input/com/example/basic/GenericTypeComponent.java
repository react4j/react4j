package com.example.basic;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class GenericTypeComponent<T>
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
