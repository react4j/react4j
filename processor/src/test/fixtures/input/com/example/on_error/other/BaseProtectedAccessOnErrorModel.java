package com.example.on_error.other;

import akasha.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import javax.annotation.Nullable;

public abstract class BaseProtectedAccessOnErrorModel
{
  @Nullable
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
