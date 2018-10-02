package com.example.prop_validate;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NonnullPropValidate
  extends Component
{
  @PropValidate
  protected void validateMyProp( @Nonnull String prop )
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
