package com.example.default_inputs;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ConstructorInputDefaultView
{
  ConstructorInputDefaultView( @Nullable @Input final String myInput )
  {
  }

  @InputDefault
  static String getMyInputDefault()
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
