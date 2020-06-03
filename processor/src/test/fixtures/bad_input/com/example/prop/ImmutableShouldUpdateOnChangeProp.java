package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutableShouldUpdateOnChangeProp
{
  @Prop( immutable = true, shouldUpdateOnChange = Feature.ENABLE )
  protected abstract String getMyKey();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
