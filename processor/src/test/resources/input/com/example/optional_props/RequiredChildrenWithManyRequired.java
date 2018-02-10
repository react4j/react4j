package com.example.optional_props;

import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class RequiredChildrenWithManyRequired
  extends Component<BaseState>
{
  @Prop
  protected abstract ReactNode[] getChildren();

  @Prop
  protected abstract String getMyRequiredProp1();

  @Prop
  protected abstract String getMyRequiredProp2();

  @Prop
  protected abstract String getMyRequiredProp3();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
