package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( name = "ZANG" )
abstract class CustomNameReactComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
