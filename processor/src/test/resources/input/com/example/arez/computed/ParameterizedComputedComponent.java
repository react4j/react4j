package com.example.arez.computed;

import arez.ComputableValue;
import arez.annotations.ComputableValueRef;
import arez.annotations.Computed;
import arez.annotations.DepType;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ParameterizedComputedComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Computed( name = "foo", observeLowerPriorityDependencies = true, keepAlive = true, depType = DepType.AREZ_OR_EXTERNAL, requireEnvironment = true, reportResult = false )
  boolean isActive()
  {
    return true;
  }

  @ComputableValueRef
  abstract ComputableValue getFooComputableValue();
}
