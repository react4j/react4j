package com.example.inject;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import sting.Named;

@ReactComponent( sting = Feature.DISABLE )
abstract class StingDisabledWithNamedConstructorParameterComponent
  extends Component
{
  StingDisabledWithNamedConstructorParameterComponent( @Named( "port" ) int port )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
