package com.example.on_prop_change.other;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;

public abstract class BaseProtectedAccessOnPropChangeModel
  extends Component
{
  @OnPropChange
  protected void onMyPropChange( String myProp )
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
