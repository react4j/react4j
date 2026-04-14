package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicateConstructorMethodImmutableProp
{
  DuplicateConstructorMethodImmutableProp( @Nullable @Input( immutable = true ) final String myProp )
  {
  }

  @Nullable
  @Input( immutable = true )
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
