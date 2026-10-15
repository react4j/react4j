package com.example.package_access.other;

import react4j.annotations.Input;
import react4j.annotations.InputDefault;

@org.jspecify.annotations.NullMarked
public abstract class BaseInputDefaultFieldModel
{
  @InputDefault( name = "myProp" )
  static final String MY_PROP = "Foo";

  @Input
  protected abstract String getMyProp();
}
