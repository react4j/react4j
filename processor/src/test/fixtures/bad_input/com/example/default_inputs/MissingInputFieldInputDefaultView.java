package com.example.default_inputs;

import react4j.ReactNode;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MissingInputFieldInputDefaultView
{
  @InputDefault
  static final String DEFAULT_MY_INPUT = "Foo";

  @Render
  ReactNode render()
  {
    return null;
  }
}
