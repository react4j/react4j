package com.example.on_error;

import react4j.JsError;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ReturnOnErrorComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  int onError( final JsError error, final ReactErrorInfo info )
  {
    return 0;
  }
}
