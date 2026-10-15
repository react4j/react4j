package com.example.prop;

import java.util.Collection;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class CollectionPropModel
{
  @Nullable
  @Input
  abstract Collection<String> getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
