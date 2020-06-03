package com.example.on_error;

import elemental2.core.JsError;
import java.io.IOException;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsOnErrorComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  void onError( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
    throws IOException
  {
  }
}
