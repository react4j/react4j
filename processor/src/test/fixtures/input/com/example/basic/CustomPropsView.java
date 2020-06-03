package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class CustomPropsView
{
  @Prop
  abstract boolean isSomeField();

  @Render
  ReactNode render()
  {
    return null;
  }
}
