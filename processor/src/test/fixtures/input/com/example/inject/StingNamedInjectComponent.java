package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;

@View
abstract class StingNamedInjectComponent
{
  StingNamedInjectComponent( @Nonnull @Named( "blah" ) String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
