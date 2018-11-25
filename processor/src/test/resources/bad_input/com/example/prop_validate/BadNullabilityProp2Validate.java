package com.example.prop_validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadNullabilityProp2Validate
  extends Component
{
  @PropValidate
  void validateMyProp( @Nullable String prop )
  {
  }

  @Prop
  @Nonnull
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
