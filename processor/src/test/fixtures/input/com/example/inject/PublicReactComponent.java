package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
public abstract class PublicReactComponent
{
  PublicReactComponent( String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
