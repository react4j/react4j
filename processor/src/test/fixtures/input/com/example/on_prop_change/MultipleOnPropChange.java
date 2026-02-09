package com.example.on_prop_change;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MultipleOnPropChange
{
  @OnInputChange
  void onPropChange( boolean myProp1, String myProp2, int myProp3 )
  {
  }

  @Input
  abstract boolean getMyProp1();

  @Nullable
  @Input
  abstract String getMyProp2();

  @Input
  abstract int getMyProp3();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
