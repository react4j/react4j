package com.example.optional_props;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ExplicitOptional
{
  @Nullable
  @Input( require = Feature.DISABLE )
  abstract String getMyOptionalProp();

  @Nullable
  @Input
  abstract String getMyRequiredProp();

  @Nullable
  @Input( require = Feature.DISABLE )
  abstract String getMyOtherOptionalProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
