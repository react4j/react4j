package com.example.prop;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ObservableViaMemoizeProp
  extends Component
{
  @Prop
  protected abstract Object getValue();

  @Memoize
  int someValue( int i )
  {
    return 0;
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
