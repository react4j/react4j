package com.example.state;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;
import react4j.core.Component;
import react4j.core.ReactNode;

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
