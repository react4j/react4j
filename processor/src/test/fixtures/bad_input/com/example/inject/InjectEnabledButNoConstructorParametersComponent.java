package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( inject = Feature.ENABLE )
abstract class InjectEnabledButNoConstructorParametersComponent
{
  InjectEnabledButNoConstructorParametersComponent()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
