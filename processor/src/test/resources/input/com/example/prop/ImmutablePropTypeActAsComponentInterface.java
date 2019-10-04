package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeActAsComponentInterface
  extends Component
{
  @ActAsComponent
  interface MyComponent
  {
  }

  @Prop( immutable = true )
  protected abstract MyComponent getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
