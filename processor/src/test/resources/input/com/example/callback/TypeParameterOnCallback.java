package com.example.callback;

import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class TypeParameterOnCallback
  extends Component
{
  @JsFunction
  public interface CustomHandler
  {
    Object render();
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback( CustomHandler.class )
  public <T> T handleFoo()
  {
    return null;
  }
}
