package com.example.default_inputs;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicateInputDefaultView2
{
  @InputDefault( name = "myInput" )
  public static String getMyInputDefault1()
  {
    return "Foo";
  }

  @InputDefault( name = "myInput" )
  public static String getMyInputDefault2()
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
