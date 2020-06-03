package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ImmutableObservableProp
{
  @Prop( immutable = true, observable = Feature.ENABLE )
  protected abstract String getMyKey();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
