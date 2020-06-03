package com.example.prop;

import arez.annotations.ArezComponent;
import java.util.ArrayList;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class CollectionArrayListArezProp
{
  @ArezComponent( allowEmpty = true )
  static abstract class AnArezElement
  {
  }

  @Prop
  protected abstract ArrayList<AnArezElement> getMyKey();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
