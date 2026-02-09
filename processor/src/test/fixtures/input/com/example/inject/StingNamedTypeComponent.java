package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;
import javax.annotation.Nullable;

@Named( "blah" )
@View
abstract class StingNamedTypeComponent
{
  StingNamedTypeComponent( @Nonnull String someParam )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
