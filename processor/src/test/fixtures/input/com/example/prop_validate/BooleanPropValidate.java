package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class BooleanPropValidate
{
  @InputValidate
  void validateMyProp( boolean prop )
  {
  }

  @Input
  abstract boolean getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
