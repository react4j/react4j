package com.example.prop_validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class BadNullabilityProp2Validate
{
  @PropValidate
  void validateMyProp( @Nullable String prop )
  {
  }

  @Prop
  @Nonnull
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
