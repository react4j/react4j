package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;

@View( sting = Feature.DISABLE )
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
