package com.example.state;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;

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
