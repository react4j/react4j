package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PropTypeBoolean
  extends Component
{
  @Prop
  protected abstract boolean getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
