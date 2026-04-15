package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeActAsComponentInterface
{
  @ActAsComponent
  interface MyComponent
  {
  }

  ImmutablePropTypeActAsComponentInterface( @Nullable @Input final MyComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
