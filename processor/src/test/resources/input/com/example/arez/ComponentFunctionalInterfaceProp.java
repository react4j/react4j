package com.example.arez;

import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ComponentFunctionalInterfaceProp
  extends ReactArezComponent
{
  @FunctionalInterface
  public interface TestFunction
  {
    void doStuff();
  }

  @Prop
  protected abstract TestFunction getValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
