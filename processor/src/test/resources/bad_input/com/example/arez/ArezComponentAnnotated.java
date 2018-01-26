package com.example.arez;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.ReactNode;

@ReactComponent
@ArezComponent( allowEmpty = true, allowConcrete = true )
abstract class ArezComponentAnnotated
  extends ReactArezComponent<BaseProps, BaseContext>
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
