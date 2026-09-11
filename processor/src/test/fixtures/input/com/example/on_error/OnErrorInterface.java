package com.example.on_error;

import react4j.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;

interface OnErrorInterface
{
  void onError( @Nonnull JsError error, @Nonnull ReactErrorInfo info );
}
