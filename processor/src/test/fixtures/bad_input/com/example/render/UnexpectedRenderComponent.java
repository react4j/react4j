package com.example.render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
@org.jspecify.annotations.NullMarked
abstract class UnexpectedRenderComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
