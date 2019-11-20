package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeKeyedViaInheritanceInterface
  extends Component
{
  interface BaseKeyedInterface
    extends Keyed
  {
  }

  interface KeyedInterface
    extends BaseKeyedInterface
  {
    void SomeOtherMethod();
  }

  @Prop( immutable = true )
  abstract KeyedInterface getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
