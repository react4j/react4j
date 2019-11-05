package com.example.inject;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class PublicReactComponent
  extends Component
{
  PublicReactComponent( String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
