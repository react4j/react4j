package com.example.post_render;

import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed2ProtectedAccessPostRenderModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedMethod" )
  @PostRender
  protected void postRender()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
