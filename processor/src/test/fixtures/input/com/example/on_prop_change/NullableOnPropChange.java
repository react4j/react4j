package com.example.on_prop_change;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NullableOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( @Nullable String myProp )
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
