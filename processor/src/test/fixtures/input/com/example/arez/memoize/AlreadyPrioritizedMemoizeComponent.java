package com.example.arez.memoize;

import arez.annotations.Memoize;
import arez.annotations.Priority;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class AlreadyPrioritizedMemoizeComponent
  extends Component
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
