package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutableObservableProp
{
  ImmutableObservableProp( @Input( observable = Feature.ENABLE ) final String myKey )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
