package com.example.prop;

import java.util.List;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class CollectionListPropModel
{
  @Prop
  abstract List<String> getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
