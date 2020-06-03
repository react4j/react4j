package com.example.inject.autofragment;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import sting.ContributeTo;

@ContributeTo( "MyAutoFragment" )
@ReactComponent
abstract class ContributeToComponent
{
  ContributeToComponent( @Nonnull String someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
