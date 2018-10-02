package com.example.package_access.other;

import react4j.Component;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;

public abstract class BasePropValidateModel
  extends Component
{
  @PropValidate
  void validateFoo( String prop )
  {
  }

  @Prop
  protected abstract String foo();
}
