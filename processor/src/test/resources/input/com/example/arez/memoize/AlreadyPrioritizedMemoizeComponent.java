package com.example.arez.memoize;

import arez.annotations.Memoize;
import arez.annotations.Priority;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class AlreadyPrioritizedMemoizeComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize( priority = Priority.NORMAL )
  String getIcon( String key )
  {
    return null;
  }
}
