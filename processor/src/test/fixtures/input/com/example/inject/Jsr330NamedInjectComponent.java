package com.example.inject;

import javax.annotation.Nonnull;
import javax.inject.Named;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class Jsr330NamedInjectComponent
  extends Component
{
  Jsr330NamedInjectComponent( @Nonnull @Named( "blah" ) String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
