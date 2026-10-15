package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ImmutablePropTypeKeyed
{
  static class KeyedComponent
    implements Keyed
  {

    public String getKey()
    {
      return "";
    }
  }

  ImmutablePropTypeKeyed( @Nullable @Input final KeyedComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
