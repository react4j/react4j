package com.example.arez;

import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class ComponentJsFunctionProp
  extends Component
{
  @JsFunction
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
