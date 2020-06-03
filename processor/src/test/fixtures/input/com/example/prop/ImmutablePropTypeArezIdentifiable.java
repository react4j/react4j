package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypeArezIdentifiable
{
  static abstract class MyComponent
    implements Identifiable<Object>
  {
    @Nonnull
    public Object getArezId()
    {
      return "";
    }
  }

  @Prop( immutable = true )
  abstract MyComponent getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
