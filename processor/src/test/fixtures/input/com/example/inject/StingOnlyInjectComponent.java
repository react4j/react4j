package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( inject = Feature.DISABLE )
abstract class StingOnlyInjectComponent
{
  StingOnlyInjectComponent( @Nonnull String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
