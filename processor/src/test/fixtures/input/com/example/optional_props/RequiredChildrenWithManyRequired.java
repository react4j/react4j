package com.example.optional_props;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class RequiredChildrenWithManyRequired
{
  @Input
  abstract ReactNode[] getChildren();

  @Input
  abstract String getMyRequiredProp1();

  @Input
  abstract String getMyRequiredProp2();

  @Input
  abstract String getMyRequiredProp3();

  @Render
  ReactNode render()
  {
    return null;
  }
}
