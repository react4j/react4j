package com.example.mixins;

import react4j.annotations.Callback;

public interface MyMixin
{
  @Callback
  default void handleFoo()
  {
  }
}
