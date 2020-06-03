package com.example.on_error;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class InverseOrderOnErrorComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  void onError( @Nonnull final ReactErrorInfo info, @Nonnull final JsError error )
  {
  }
}
