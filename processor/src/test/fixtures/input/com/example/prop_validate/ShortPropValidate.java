package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ShortPropValidate
{
  @PropValidate
  void validateMyProp( short prop )
  {
  }

  @Prop
  abstract short getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
