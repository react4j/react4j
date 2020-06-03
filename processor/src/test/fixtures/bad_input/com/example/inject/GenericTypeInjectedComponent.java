package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class GenericTypeInjectedComponent<T>
{
  GenericTypeInjectedComponent( String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
