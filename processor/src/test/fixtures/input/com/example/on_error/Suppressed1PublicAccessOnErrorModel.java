package com.example.on_error;

import react4j.JsError;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class Suppressed1PublicAccessOnErrorModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @OnError
  public void onError( final JsError error, final ReactErrorInfo info )
  {
  }
}
