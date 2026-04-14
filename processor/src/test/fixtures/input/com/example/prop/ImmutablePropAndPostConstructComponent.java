package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@SuppressWarnings( "React4j:MethodBasedImmutableInput" )
@View
abstract class ImmutablePropAndPostConstructComponent
{
  ImmutablePropAndPostConstructComponent( @Nullable @Input( immutable = true ) final String myProp )
  {
  }

  @PostConstruct
  void postConstruct()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
