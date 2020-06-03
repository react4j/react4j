package com.example.prop_validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class BadNullabilityProp1Validate
{
  @PropValidate
  void validateMyProp( @Nonnull String prop )
  {
  }

  @Prop
  @Nullable
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
