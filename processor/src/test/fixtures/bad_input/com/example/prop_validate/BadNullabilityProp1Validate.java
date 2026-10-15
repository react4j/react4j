package com.example.prop_validate;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class BadNullabilityProp1Validate
{
  @InputValidate
  void validateMyProp( String prop )
  {
  }

  @Input
  @Nullable
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
