package com.example.default_inputs;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PrivateMethodInputDefaultView
{
  @InputDefault
  private static String getMyInputDefault()
  {
    return "Foo";
  }

  @Input
  protected abstract String getMyInput();

  @Render
  ReactNode render()
  {
    return null;
  }
}
