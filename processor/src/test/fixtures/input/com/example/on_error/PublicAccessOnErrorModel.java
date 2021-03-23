package com.example.on_error;

import akasha.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PublicAccessOnErrorModel
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  public void onError( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }
}
