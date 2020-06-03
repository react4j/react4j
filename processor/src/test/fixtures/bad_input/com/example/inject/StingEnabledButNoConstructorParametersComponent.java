package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( sting = Feature.ENABLE )
abstract class StingEnabledButNoConstructorParametersComponent
{
  StingEnabledButNoConstructorParametersComponent()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
