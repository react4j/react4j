package com.example.default_inputs;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DefaultMethodContextInputView
{
  @Input( source = Input.Source.CONTEXT )
  abstract String getMyInput();

  @InputDefault
  public static String getMyInputDefault()
  {
    return "Foo";
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
