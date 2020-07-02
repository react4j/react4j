package com.example.render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( requireRender = false )
abstract class UnexpectedRenderComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
