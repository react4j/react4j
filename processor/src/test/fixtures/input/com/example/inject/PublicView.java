package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
public abstract class PublicView
{
  PublicView( String someParam )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
