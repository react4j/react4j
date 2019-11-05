package com.example.prop_validate;

import java.util.ArrayList;
import java.util.BitSet;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class OtherPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( BitSet prop )
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
