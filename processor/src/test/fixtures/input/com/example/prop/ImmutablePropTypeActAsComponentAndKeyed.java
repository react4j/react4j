package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeActAsComponentAndKeyed
{
  @ActAsComponent
  interface MyComponent
    extends Keyed
  {
  }

  // Keyed should win over ArezId for immutable prop
  ImmutablePropTypeActAsComponentAndKeyed( @Nullable @Input final MyComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
