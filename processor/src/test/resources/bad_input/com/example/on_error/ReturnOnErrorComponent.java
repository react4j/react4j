package com.example.on_error;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ReturnsOnErrorComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @OnError
  int onError( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
    return 0;
  }
}
