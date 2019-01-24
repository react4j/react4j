package com.example.inject;

import arez.annotations.PostConstruct;
import javax.inject.Inject;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class MethodInjectPostConstructOnReactComponent
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

  @PostConstruct
  void someAutorun()
  {
  }
}
