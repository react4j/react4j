package com.example.prop;

import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeKeyedInterface
{
  interface KeyedInterface
    extends Keyed
  {
    void SomeOtherMethod();
  }

  ImmutablePropTypeKeyedInterface( @Nullable @Input final KeyedInterface myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
