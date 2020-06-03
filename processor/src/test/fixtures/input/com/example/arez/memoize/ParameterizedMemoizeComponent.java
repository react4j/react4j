package com.example.arez.memoize;

import arez.annotations.DepType;
import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ParameterizedMemoizeComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Memoize( name = "foo", observeLowerPriorityDependencies = true, keepAlive = true, depType = DepType.AREZ_OR_NONE, reportResult = false )
  String getIcon()
  {
    return null;
  }
}
