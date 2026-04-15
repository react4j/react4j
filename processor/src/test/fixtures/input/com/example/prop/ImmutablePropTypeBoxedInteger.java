package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@SuppressWarnings( "React4j:MethodBasedImmutableInput" )
@View
abstract class ImmutablePropTypeBoxedInteger
{
  @Nullable
  @Input( immutable = true )
  abstract Integer getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
