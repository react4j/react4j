package com.example.arez.memoize;

import arez.annotations.DepType;
import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class ParameterizedMemoizeComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize( name = "foo", observeLowerPriorityDependencies = true, keepAlive = true, depType = DepType.AREZ_OR_NONE, reportResult = false )
  String getIcon()
  {
    return null;
  }
}
