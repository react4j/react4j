package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class MutablePropAndOnChangeAndPostConstructComponent
{
  @Prop
  abstract String getMyProp();

  @PostConstruct
  void postConstruct()
  {
  }

  @OnPropChange
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
