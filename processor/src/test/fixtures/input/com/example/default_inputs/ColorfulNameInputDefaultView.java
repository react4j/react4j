package com.example.default_inputs;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class ColorfulNameInputDefaultView
{
  @InputDefault
  static final String DEFAULT_MY_PROP12$23 = "Foo";

  @Input
  @Nonnull
  abstract String getMyProp12$23();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
