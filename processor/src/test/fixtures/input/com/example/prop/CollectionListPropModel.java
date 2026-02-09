package com.example.prop;

import java.util.List;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class CollectionListPropModel
{
  @Nullable
  @Input
  abstract List<String> getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
