package com.example.on_error.other;

import react4j.JsError;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import org.jspecify.annotations.Nullable;

@org.jspecify.annotations.NullMarked
public abstract class BaseProtectedAccessOnErrorModel
{
  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }

  @OnError
  protected void onError( final JsError error, final ReactErrorInfo info )
  {
  }
}
