package com.example.on_error;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadParam2OnErrorComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @OnError
  void onError( @Nonnull final JsError error1, @Nonnull final JsError error2 )
  {
  }
}
