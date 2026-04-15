package com.example.prop;

import arez.annotations.ArezComponentLike;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeArezComponentLikeClass
{
  @ArezComponentLike
  static abstract class MyComponent
  {
  }

  ImmutablePropTypeArezComponentLikeClass( @Nullable @Input final MyComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
