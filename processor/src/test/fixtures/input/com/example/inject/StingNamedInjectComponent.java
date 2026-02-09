package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;
import javax.annotation.Nullable;

@View
abstract class StingNamedInjectComponent
{
  StingNamedInjectComponent( @Nonnull @Named( "blah" ) String someParam )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
