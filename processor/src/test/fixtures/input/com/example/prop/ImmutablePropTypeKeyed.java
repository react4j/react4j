package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeKeyed
  extends Component
{
  static class KeyedComponent
    implements Keyed
  {
    @Nonnull
    @Override
    public String getKey()
    {
      return "";
    }
  }

  @Prop( immutable = true )
  abstract KeyedComponent getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
