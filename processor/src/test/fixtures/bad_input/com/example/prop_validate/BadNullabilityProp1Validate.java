package com.example.prop_validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadNullabilityProp1Validate
  extends Component
{
  @PropValidate
  void validateMyProp( @Nonnull String prop )
  {
  }

  @Prop
  @Nullable
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
