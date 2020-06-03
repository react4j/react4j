package com.example.arez;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ComponentFunctionalInterfaceProp
{
  @FunctionalInterface
  public interface TestFunction
  {
    void doStuff();
  }

  @Prop
  abstract TestFunction getValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
