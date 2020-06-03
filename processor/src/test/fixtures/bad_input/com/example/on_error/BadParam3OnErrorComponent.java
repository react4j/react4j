package com.example.on_error;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class BadParam3OnErrorComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  void onError( @Nonnull final JsError error, final int value )
  {
  }
}
