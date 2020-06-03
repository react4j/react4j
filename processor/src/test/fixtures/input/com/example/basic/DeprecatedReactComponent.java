package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@Deprecated
@ReactComponent
abstract class DeprecatedReactComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
