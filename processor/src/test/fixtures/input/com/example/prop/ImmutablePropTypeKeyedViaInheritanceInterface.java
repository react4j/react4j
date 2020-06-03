package com.example.prop;

import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
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

  @Prop( immutable = true )
  abstract KeyedInterface getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
