package com.example.inject;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent( sting = Feature.DISABLE )
abstract class Jsr330OnlyInjectComponent
  extends Component
{
  Jsr330OnlyInjectComponent( @Nonnull String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
