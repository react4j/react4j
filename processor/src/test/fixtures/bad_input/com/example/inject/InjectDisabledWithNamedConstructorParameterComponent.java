package com.example.inject;

import javax.inject.Named;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent( inject = Feature.DISABLE )
abstract class InjectDisabledWithNamedConstructorParameterComponent
  extends Component
{
  InjectDisabledWithNamedConstructorParameterComponent( @Named( "port" ) int port )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
