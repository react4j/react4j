package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
public abstract class PublicReactComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
