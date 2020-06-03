package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class MutablePropAndPostConstructComponent
{
  @Prop
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
