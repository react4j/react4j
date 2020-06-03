package com.example.prop;

import arez.annotations.ArezComponent;
import java.util.Set;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class CollectionSetArezProp
{
  @ArezComponent( allowEmpty = true )
  static abstract class AnArezElement
  {
  }

  @Prop
  protected abstract Set<AnArezElement> getMyKey();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
