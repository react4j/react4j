package com.example.arez;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ComponentShouldUpdateOnChangeProp
{
  @Prop( shouldUpdateOnChange = Feature.ENABLE )
  abstract String getValue();

  @Prop( shouldUpdateOnChange = Feature.DISABLE )
  abstract String getOther();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
