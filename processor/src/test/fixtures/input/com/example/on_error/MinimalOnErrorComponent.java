package com.example.on_error;

import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class MinimalOnErrorComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  void onError()
  {
  }
}
