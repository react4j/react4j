package com.example.default_inputs;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ColorfulNameInputDefaultView
{
  @InputDefault
  static final String DEFAULT_MY_PROP12$23 = "Foo";

  @Input
  abstract String getMyProp12$23();

  @Render
  ReactNode render()
  {
    return null;
  }
}
