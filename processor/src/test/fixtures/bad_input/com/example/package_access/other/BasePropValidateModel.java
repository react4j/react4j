package com.example.package_access.other;

import react4j.annotations.Prop;
import react4j.annotations.PropValidate;

public abstract class BasePropValidateModel
{
  @PropValidate
  void validateFoo( String prop )
  {
  }

  @Prop
  protected abstract String foo();
}
