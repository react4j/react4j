package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ImmutablePropTypeKeyed
{
  static class KeyedComponent
    implements Keyed
  {
    @Nonnull
    public String getKey()
    {
      return "";
    }
  }

  @Prop( immutable = true )
  abstract KeyedComponent getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
