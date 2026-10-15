package com.example.on_error;

import react4j.JsError;
import react4j.ReactErrorInfo;

@org.jspecify.annotations.NullMarked
interface OnErrorInterface
{
  void onError( JsError error, ReactErrorInfo info );
}
