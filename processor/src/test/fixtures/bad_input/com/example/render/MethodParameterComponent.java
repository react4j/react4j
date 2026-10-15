package com.example.render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class MethodParameterComponent
{
  @Render
  ReactNode render( int i )
  {
    return null;
  }
}
