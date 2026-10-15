package com.example.prop_validate;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class BadNullabilityProp2Validate
{
  @InputValidate
  void validateMyProp( @Nullable String prop )
  {
  }

  @Input

  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
