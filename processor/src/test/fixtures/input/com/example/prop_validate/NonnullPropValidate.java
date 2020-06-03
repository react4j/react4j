package com.example.prop_validate;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class NonnullPropValidate
{
  @PropValidate
  void validateMyProp( @Nonnull String prop )
  {
  }

  @Prop
  @Nonnull
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
