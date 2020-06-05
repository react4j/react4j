package com.example.default_inputs;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ProtectedMethodInputDefaultView
{
  @Nonnull
  @InputDefault
  protected static String getMyPropDefault()
  {
    return "Foo";
  }

  @Input
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
