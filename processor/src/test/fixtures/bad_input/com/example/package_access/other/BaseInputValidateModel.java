package com.example.package_access.other;

import react4j.annotations.Input;
import react4j.annotations.InputValidate;

public abstract class BaseInputValidateModel
{
  @InputValidate
  void validateFoo( String prop )
  {
  }

  @Input
  protected abstract String foo();
}
