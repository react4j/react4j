package com.example.pre_render;

import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed2PublicAccessPreRenderModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicMethod" )
  @PreRender
  public void preRender()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
