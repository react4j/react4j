package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeKeyedInterface
  extends Component
{
  interface KeyedInterface
    extends Keyed
  {
    void SomeOtherMethod();
  }

  @Prop( immutable = true )
  protected abstract KeyedInterface getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
