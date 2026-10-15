package com.example.on_error;

import react4j.JsError;
import java.io.IOException;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ThrowsOnErrorComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  void onError( final JsError error, final ReactErrorInfo info )
    throws IOException
  {
  }
}
