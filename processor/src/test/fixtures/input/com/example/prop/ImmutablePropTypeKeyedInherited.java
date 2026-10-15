package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ImmutablePropTypeKeyedInherited
{
  static class BaseComponent
    implements Keyed
  {

    public String getKey()
    {
      return "";
    }
  }

  static class KeyedComponent
    extends BaseComponent
  {
  }

  ImmutablePropTypeKeyedInherited( @Nullable @Input final KeyedComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
