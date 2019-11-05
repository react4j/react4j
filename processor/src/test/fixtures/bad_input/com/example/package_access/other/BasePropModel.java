package com.example.package_access.other;

import react4j.Component;
import react4j.annotations.Prop;

public abstract class BasePropModel
  extends Component
{
  @Prop
  abstract String foo();
}
