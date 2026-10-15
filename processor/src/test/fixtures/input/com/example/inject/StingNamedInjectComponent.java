package com.example.inject;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class StingNamedInjectComponent
{
  StingNamedInjectComponent( @Named( "blah" ) String someParam )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
