package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeKeyedInherited
  extends Component
{
  static class BaseComponent
    implements Keyed
  {
    @Nonnull
    @Override
    public String getKey()
    {
      return "";
    }
  }

  static class KeyedComponent
    extends BaseComponent
  {
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
