package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@SuppressWarnings( "React4j:MethodBasedImmutableInput" )
@View
abstract class ExplicitDependencyEnableInput
{
  @ArezComponent( allowEmpty = true )
  static abstract class MyComponent
  {
  }

  ExplicitDependencyEnableInput( @Nullable @Input( immutable = true, dependency = Feature.ENABLE ) final MyComponent myComponent )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
