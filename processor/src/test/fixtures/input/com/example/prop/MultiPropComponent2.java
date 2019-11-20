package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MultiPropComponent2
  extends Component
{
  @Prop
  abstract String getMyProp();

  @Prop
  abstract ReactNode[] getChildren();

  @Prop
  abstract String getMyProp2();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
