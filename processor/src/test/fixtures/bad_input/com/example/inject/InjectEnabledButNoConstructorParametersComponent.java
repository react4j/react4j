package com.example.inject;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ConstructorInjectComponent
  extends Component
{
  ConstructorInjectComponent( @Nonnull String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
