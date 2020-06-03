package com.example.inject;

import javax.annotation.Nonnull;
import javax.inject.Named;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class Jsr330NamedInjectComponent
{
  Jsr330NamedInjectComponent( @Nonnull @Named( "blah" ) String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
