package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PropTypeChar
  extends Component
{
  @Prop
  abstract char getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
