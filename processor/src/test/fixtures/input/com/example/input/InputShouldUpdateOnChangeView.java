package com.example.input;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class InputShouldUpdateOnChangeView
{
  @Input( shouldUpdateOnChange = Feature.ENABLE )
  abstract String getValue();

  @Input( shouldUpdateOnChange = Feature.DISABLE )
  abstract String getOther();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
