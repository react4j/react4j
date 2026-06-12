package com.example.component;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( name = "for", type = View.Type.STATEFUL )
abstract class BadNameComponent2
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
