package com.example.component;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( name = "for" )
abstract class BadNameComponent2
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
