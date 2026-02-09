package com.example.prop_validate;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ExplicitNamePropValidate
{
  @InputValidate( name = "myProp" )
  void zapzap( String prop )
  {
  }

  @Nullable
  @Input
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
