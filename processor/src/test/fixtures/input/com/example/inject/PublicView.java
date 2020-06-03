package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
public abstract class PublicView
{
  PublicView( String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
