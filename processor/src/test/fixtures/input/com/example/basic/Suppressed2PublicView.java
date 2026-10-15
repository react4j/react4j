package com.example.basic;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@SuppressReact4jWarnings( "React4j:PublicView" )
@View( exportBuilder = true, type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
public abstract class Suppressed2PublicView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
