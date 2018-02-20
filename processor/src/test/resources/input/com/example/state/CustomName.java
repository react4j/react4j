package com.example.state;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class CustomName
  extends Component
{
  @State( name = "foo" )
  protected abstract String getMyValue();

  @State( name = "foo" )
  protected abstract void setMyValue( String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
