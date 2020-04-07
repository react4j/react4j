package com.example.inject.autofragment;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import sting.ContributeTo;
import sting.Named;

@ContributeTo( "MyAutoFragment" )
@ReactComponent
abstract class ContributeToComponent
  extends Component
{
  ContributeToComponent( @Nonnull String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
