package com.example.default_inputs;

import react4j.ReactNode;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class MissingInputMethodInputDefaultView
{
  @InputDefault
  static String getMyInputDefault()
  {
    return "Foo";
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
