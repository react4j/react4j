package com.example.arez;

import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class ComponentJsFunctionProp
{
  @JsFunction
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
