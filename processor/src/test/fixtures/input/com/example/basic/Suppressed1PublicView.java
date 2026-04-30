package com.example.basic;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@SuppressWarnings( "React4j:PublicView" )
@View( exportBuilder = true )
public abstract class Suppressed1PublicView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
