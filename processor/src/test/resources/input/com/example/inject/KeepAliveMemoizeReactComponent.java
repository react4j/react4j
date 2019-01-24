package com.example.inject;

import arez.annotations.Memoize;
import javax.inject.Inject;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class KeepAliveMemoizeReactComponent
  extends Component
{
  @Inject
  String someParam;

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize( keepAlive = true )
  int someValue()
  {
    return 0;
  }
}
