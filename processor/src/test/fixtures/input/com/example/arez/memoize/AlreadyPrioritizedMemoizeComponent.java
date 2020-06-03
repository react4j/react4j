package com.example.arez.memoize;

import arez.annotations.Memoize;
import arez.annotations.Priority;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class AlreadyPrioritizedMemoizeComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Memoize( priority = Priority.NORMAL )
  String getIcon( String key )
  {
    return null;
  }
}
