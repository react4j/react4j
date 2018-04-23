package com.example.package_access.other;

import react4j.annotations.Prop;
import react4j.core.Component;

public abstract class BasePropModel
  extends Component
{
  @Prop
  abstract String foo();
}
