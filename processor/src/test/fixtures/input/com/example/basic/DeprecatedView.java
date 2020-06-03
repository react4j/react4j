package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@Deprecated
@View
abstract class DeprecatedView
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
