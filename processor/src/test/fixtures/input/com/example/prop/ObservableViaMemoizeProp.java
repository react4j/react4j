package com.example.prop;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ObservableViaMemoizeProp
{
  @Prop
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
