package com.example.prop_validate.other;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;

@org.jspecify.annotations.NullMarked
public abstract class BaseProtectedAccessPropValidateModel
{
  @InputValidate
  protected void validateMyProp( String prop )
  {
  }

  @Nullable
  @Input
  protected abstract String getMyProp();

  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }
}
