package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropAndPostConstructComponent
{
  @Prop( immutable = true )
  abstract String getMyProp();

  @PostConstruct
  void postConstruct()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
