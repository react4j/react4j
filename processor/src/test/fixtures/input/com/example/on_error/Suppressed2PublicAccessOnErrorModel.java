package com.example.on_error;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;

@ReactComponent
abstract class Suppressed2PublicAccessOnErrorModel
{
  @Render
  ReactNode render()
  {
    return null;
  }

  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicMethod" )
  @OnError
  public void onError( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }
}
