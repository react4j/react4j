package com.example.post_render;

import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed1PublicAccessPostRenderModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @PostRender
  public void postRender()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
