package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View( name = "ZANG" )
abstract class CustomNameView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
