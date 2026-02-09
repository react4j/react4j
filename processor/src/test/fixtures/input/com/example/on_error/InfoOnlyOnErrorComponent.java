package com.example.on_error;

import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class InfoOnlyOnErrorComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  void onError( @Nonnull final ReactErrorInfo info )
  {
  }
}
