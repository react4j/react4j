package com.example.default_inputs;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PrivateFieldInputDefaultView
{
  @InputDefault
  private static final String DEFAULT_MY_INPUT = "Foo";

  @Input
  protected abstract String getMyInput();

  @Render
  ReactNode render()
  {
    return null;
  }
}
