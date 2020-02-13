package com.example.inject;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import sting.Named;

@ReactComponent
abstract class StingNamedInjectComponent
  extends Component
{
  StingNamedInjectComponent( @Nonnull @Named( "blah" ) String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
