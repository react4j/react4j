package com.example.prop_validate;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class NonnullPropValidate
{
  @InputValidate
  void validateMyProp( @Nonnull String prop )
  {
  }

  @Input
  @Nonnull
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
