package com.example.prop;

import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ImmutablePropTypeKeyedMultiStepInherited
{
  static class BaseComponent
    implements Keyed
  {

    public String getKey()
    {
      return "";
    }
  }

  static class MiddleComponent
    extends BaseComponent
  {
  }

  static class KeyedComponent
    extends MiddleComponent
  {
  }

  ImmutablePropTypeKeyedMultiStepInherited( @Nullable @Input final KeyedComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
