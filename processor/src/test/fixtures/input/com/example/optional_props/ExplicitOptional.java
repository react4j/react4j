package com.example.optional_props;

import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ExplicitOptional
{
  @Input( require = Feature.DISABLE )
  abstract String getMyOptionalProp();

  @Input
  abstract String getMyRequiredProp();

  @Input( require = Feature.DISABLE )
  abstract String getMyOtherOptionalProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
