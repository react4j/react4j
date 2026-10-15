package com.example.prop;

import arez.component.Identifiable;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ImmutablePropTypeArezIdentifiable
{
  static abstract class MyComponent
    implements Identifiable<Object>
  {

    public Object getArezId()
    {
      return "";
    }
  }

  ImmutablePropTypeArezIdentifiable( @Nullable @Input final MyComponent myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
