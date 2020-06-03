package com.example.prop;

import java.util.HashSet;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class CollectionHashSetPropModel
{
  @Prop
  abstract HashSet<String> getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
