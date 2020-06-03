package com.example.on_prop_change;

import java.util.ArrayList;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ParameterizedOnPropChange
{
  @OnPropChange
  void onMyPropChange( ArrayList<String> myProp )
  {
  }

  @Prop
  abstract ArrayList<String> getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
