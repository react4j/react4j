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
  abstract ReactNode[] getChildren();

  @Prop
  abstract String getMyRequiredProp1();

  @Prop
  abstract String getMyRequiredProp2();

  @Prop
  abstract String getMyRequiredProp3();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
