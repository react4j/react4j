package com.example.on_prop_change;

import java.util.BitSet;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class OtherTypeOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( BitSet myProp )
  {
  }

  @Prop
  protected abstract BitSet getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
