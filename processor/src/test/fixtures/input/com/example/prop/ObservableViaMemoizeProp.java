package com.example.prop;

import arez.annotations.Memoize;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ObservableViaMemoizeProp
{
  @Nullable
  @Input
  abstract Object getValue();

  @Memoize
  int someValue( int i )
  {
    return 0;
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
