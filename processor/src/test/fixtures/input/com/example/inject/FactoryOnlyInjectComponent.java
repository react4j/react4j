package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( inject = Feature.DISABLE, sting = Feature.DISABLE )
abstract class FactoryOnlyInjectComponent
{
  FactoryOnlyInjectComponent( @Nonnull String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
