package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ConstructorInjectComponent
{
  ConstructorInjectComponent( @Nonnull String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
