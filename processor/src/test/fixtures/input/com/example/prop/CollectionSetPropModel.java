package com.example.prop;

import java.util.Set;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class CollectionSetPropModel
{
  @Nullable
  @Input
  abstract Set<String> getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
