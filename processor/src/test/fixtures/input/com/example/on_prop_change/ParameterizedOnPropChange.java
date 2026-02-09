package com.example.on_prop_change;

import java.util.ArrayList;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ParameterizedOnPropChange
{
  @OnInputChange
  void onMyPropChange( ArrayList<String> myProp )
  {
  }

  @Nullable
  @Input
  abstract ArrayList<String> getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
