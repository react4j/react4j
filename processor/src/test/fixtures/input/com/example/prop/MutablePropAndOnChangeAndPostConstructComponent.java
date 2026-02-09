package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class MutablePropAndOnChangeAndPostConstructComponent
{
  @Nullable
  @Input
  abstract String getMyProp();

  @PostConstruct
  void postConstruct()
  {
  }

  @OnInputChange
  void onMyPropChange( String myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
