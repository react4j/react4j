package com.example.on_error.other;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessOnErrorModel
{
  @Render
  protected ReactNode render()
  {
    return null;
  }

  @OnError
  protected void onError( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }
}
