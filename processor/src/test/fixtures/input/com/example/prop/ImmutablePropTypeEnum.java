package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ImmutablePropTypeEnum
{
  enum Foo
  {
    A, B, C
  }

  @Prop( immutable = true )
  abstract Foo getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
