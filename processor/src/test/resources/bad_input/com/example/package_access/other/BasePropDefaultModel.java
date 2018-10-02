package com.example.package_access.other;

import react4j.Component;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;

public abstract class BasePropDefaultModel
  extends Component
{
  @PropDefault
  static String getMyPropDefault()
  {
    return "Foo";
  }

  @Prop
  protected abstract String getMyProp();
}
