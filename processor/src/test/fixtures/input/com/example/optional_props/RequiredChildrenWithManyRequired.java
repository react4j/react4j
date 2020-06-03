package com.example.optional_props;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class RequiredChildrenWithManyRequired
{
  @Prop
  abstract ReactNode[] getChildren();

  @Prop
  abstract String getMyRequiredProp1();

  @Prop
  abstract String getMyRequiredProp2();

  @Prop
  abstract String getMyRequiredProp3();

  @Render
  ReactNode render()
  {
    return null;
  }
}
