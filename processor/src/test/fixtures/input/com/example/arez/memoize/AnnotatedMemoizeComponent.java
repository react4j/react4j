package com.example.arez.memoize;

import arez.annotations.Memoize;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class AnnotatedMemoizeComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Memoize
  @Nullable
  String getIcon( @Nonnull String key )
  {
    return null;
  }
}
