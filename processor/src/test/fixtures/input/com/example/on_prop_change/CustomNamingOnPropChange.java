package com.example.on_prop_change;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class CustomNamingOnPropChange
{
  @OnInputChange
  void onPropChange( boolean lastMyProp1 )
  {
  }

  @OnInputChange
  void onPropChange( String prevMyProp2 )
  {
  }

  @OnInputChange
  void onPropChange( int myProp3 )
  {
  }

  @Input
  abstract boolean getMyProp1();

  @Input
  abstract String getMyProp2();

  @Input
  abstract int getMyProp3();

  @Render
  ReactNode render()
  {
    return null;
  }
}
