package com.example.inject.autofragment;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.ContributeTo;

@ContributeTo( "MyAutoFragment" )
@View
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
