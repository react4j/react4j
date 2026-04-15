package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutableMixedInputModel
{
  ImmutableMixedInputModel( @Nullable @Input final String myImmutableProp )
  {
  }

  @Nullable
  @Input
  abstract String getMyMutableProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
