package com.example.prop;

import arez.annotations.ArezComponentLike;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ImmutablePropTypeArezComponentLikeInterface
{
  @ArezComponentLike
  interface MyComponent
  {
  }

  ImmutablePropTypeArezComponentLikeInterface( @Nullable @Input final MyComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
