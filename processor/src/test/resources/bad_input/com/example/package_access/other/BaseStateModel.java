package com.example.package_access.other;

import react4j.Component;
import react4j.annotations.State;

public abstract class BaseStateModel
  extends Component
{
  @State
  abstract String getFoo();

  abstract void setFoo( String v );
}
