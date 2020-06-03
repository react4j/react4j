package com.example.arez;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ComponentShouldNotUpdateOnChangeProp
{
  @Prop( shouldUpdateOnChange = Feature.DISABLE )
  abstract String getValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
