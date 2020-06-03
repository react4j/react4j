package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class LongPropValidate
{
  @PropValidate
  void validateMyProp( long prop )
  {
  }

  @Prop
  abstract long getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
