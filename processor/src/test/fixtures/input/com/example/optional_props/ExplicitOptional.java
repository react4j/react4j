package com.example.optional_props;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
