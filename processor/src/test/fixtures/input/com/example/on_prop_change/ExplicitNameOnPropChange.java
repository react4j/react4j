package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputRef;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ExplicitNameOnPropChange
{
  @OnInputChange
  void onMyInputChange( @InputRef( "myProp" ) double zzzz )
  {
  }

  @Input
  abstract double getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
