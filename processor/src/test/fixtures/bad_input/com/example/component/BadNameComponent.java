package com.example.component;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( name = "-abc" )
abstract class BadNameComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
