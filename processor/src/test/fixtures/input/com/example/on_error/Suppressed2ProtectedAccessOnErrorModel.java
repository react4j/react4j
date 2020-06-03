package com.example.on_error;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@View
abstract class Suppressed2ProtectedAccessOnErrorModel
{
  @Render
  ReactNode render()
  {
    return null;
  }

  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedMethod" )
  @OnError
  protected void onError( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }
}
