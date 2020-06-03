package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class FloatPropValidate
{
  @PropValidate
  void validateMyProp( float prop )
  {
  }

  @Prop
  abstract float getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
