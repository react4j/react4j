package com.example.render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class ThrowsComponent
{
  @Render
  ReactNode render()
    throws Exception
  {
    return null;
  }
}
