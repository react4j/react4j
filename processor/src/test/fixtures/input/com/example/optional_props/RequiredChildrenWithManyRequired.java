package com.example.optional_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class RequiredChildrenWithManyRequired
  extends Component
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
