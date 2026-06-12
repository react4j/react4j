package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
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
