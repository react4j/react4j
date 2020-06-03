package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ImmutablePropTypeActAsComponentAndKeyed
{
  @ActAsComponent
  interface MyComponent
    extends Keyed
  {
  }

  // Keyed should win over ArezId for immutable prop
  @Prop( immutable = true )
  abstract MyComponent getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
