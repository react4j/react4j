package com.example.arez.memoize;

import arez.annotations.Memoize;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
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
