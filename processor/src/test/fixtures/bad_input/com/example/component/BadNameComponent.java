package com.example.component;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( name = "-abc" )
abstract class BadNameComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
