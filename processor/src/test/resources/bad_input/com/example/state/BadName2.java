package com.example.state;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;

@ReactComponent
abstract class BadName2
  extends Component
{
  @State
  protected abstract String getImport();

  @State
  protected abstract void setImport( String value );

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
