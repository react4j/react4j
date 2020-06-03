package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BasicView
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
