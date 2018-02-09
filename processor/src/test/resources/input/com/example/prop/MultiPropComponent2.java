package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class MultiPropComponent2
  extends Component<BaseState>
{
  @Prop
  protected abstract String getMyProp();

  @Prop
  protected abstract ReactNode[] getChildren();

  @Prop
  protected abstract String getMyProp2();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
