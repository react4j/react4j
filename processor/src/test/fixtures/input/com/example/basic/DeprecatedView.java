package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@Deprecated
@View
abstract class DeprecatedView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
