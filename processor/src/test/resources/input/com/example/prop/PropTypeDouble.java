package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class PropTypeDouble
  extends Component<BaseState>
{
  @Prop
  protected abstract double getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
