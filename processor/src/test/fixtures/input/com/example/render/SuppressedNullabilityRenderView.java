package com.example.render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class SuppressedNullabilityRenderView
{
  @SuppressWarnings( "React4j:MissingRenderNullability" )
  @Render
  ReactNode render()
  {
    return null;
  }
}
