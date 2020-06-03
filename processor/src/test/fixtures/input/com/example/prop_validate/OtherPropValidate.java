package com.example.prop_validate;

import java.util.BitSet;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class OtherPropValidate
{
  @PropValidate
  void validateMyProp( BitSet prop )
  {
  }

  @Prop
  abstract BitSet getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
