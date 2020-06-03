package com.example.render;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;

abstract class MyParent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
