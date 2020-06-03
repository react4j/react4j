package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import sting.Named;

@ReactComponent( sting = Feature.DISABLE )
abstract class StingDisabledWithNamedConstructorParameterComponent
{
  StingDisabledWithNamedConstructorParameterComponent( @Named( "port" ) int port )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
