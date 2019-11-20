package com.example.on_prop_change;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NonnullOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( @Nonnull String myProp )
  {
  }

  @Prop
  @Nonnull
  abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
