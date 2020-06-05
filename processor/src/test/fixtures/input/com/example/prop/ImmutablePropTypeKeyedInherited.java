package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeKeyedInherited
{
  static class BaseComponent
    implements Keyed
  {
    @Nonnull
    public String getKey()
    {
      return "";
    }
  }

  static class KeyedComponent
    extends BaseComponent
  {
  }

  @Input( immutable = true )
  abstract KeyedComponent getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
