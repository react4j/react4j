package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeActAsComponentAndKeyed
  extends Component
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
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
