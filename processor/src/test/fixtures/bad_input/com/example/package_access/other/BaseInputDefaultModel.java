package com.example.package_access.other;

import react4j.annotations.Input;
import react4j.annotations.InputDefault;

public abstract class BaseInputDefaultModel
{
  @InputDefault
  static String getMyPropDefault()
  {
    return "Foo";
  }

  @Input
  protected abstract String getMyProp();
}
