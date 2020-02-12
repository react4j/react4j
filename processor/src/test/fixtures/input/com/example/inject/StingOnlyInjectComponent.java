package com.example.inject;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent( inject = Feature.DISABLE )
abstract class StingOnlyInjectComponent
  extends Component
{
  StingOnlyInjectComponent( @Nonnull String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
