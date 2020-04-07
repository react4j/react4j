package com.example.inject;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import sting.Named;

@Named( "blah" )
@ReactComponent
abstract class StingNamedTypeComponent
  extends Component
{
  StingNamedTypeComponent( @Nonnull String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
