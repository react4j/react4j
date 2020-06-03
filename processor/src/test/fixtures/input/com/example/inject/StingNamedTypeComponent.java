package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import sting.Named;

@Named( "blah" )
@ReactComponent
abstract class StingNamedTypeComponent
{
  StingNamedTypeComponent( @Nonnull String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
