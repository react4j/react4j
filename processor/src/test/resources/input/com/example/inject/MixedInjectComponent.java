package com.example.inject;

import javax.inject.Inject;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MixedInjectComponent
  extends Component
{
  @Inject
  String someParam;

  MixedInjectComponent( final Integer anotherParam, String blah )
  {
  }

  @Inject
  public void setFoo( String foo )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
