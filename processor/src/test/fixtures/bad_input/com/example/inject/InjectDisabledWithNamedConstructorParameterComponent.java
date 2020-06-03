package com.example.inject;

import javax.inject.Named;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( inject = Feature.DISABLE )
abstract class InjectDisabledWithNamedConstructorParameterComponent
{
  InjectDisabledWithNamedConstructorParameterComponent( @Named( "port" ) int port )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
