package com.example.prop;

import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeKeyedViaInheritanceInterface
{
  interface BaseKeyedInterface
    extends Keyed
  {
  }

  interface KeyedInterface
    extends BaseKeyedInterface
  {
    void SomeOtherMethod();
  }

  @Input( immutable = true )
  abstract KeyedInterface getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
