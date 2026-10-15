package com.example.prop;

import arez.annotations.PostConstruct;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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
  void onMyPropChange( @Nullable String myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
