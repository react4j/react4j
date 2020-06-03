package com.example.on_error;

import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicateOnErrorComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  void onError1()
  {
  }

  @OnError
  void onError2()
  {
  }
}
