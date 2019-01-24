package com.example.inject;

import arez.annotations.Observe;
import javax.inject.Inject;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class MethodInjectObserveOnReactComponent
  extends Component
{
  @Inject
  public void setFoo( String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Observe
  void someAutorun()
  {
  }
}
