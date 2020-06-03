package com.example.prop;

import arez.annotations.ArezComponent;
import java.util.HashSet;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class CollectionHashSetArezProp
{
  @ArezComponent( allowEmpty = true )
  static abstract class AnArezElement
  {
  }

  @Prop
  protected abstract HashSet<AnArezElement> getMyKey();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
