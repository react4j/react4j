package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactElement;

@ReactComponent
@ArezComponent( name = "X", allowEmpty = true )
class ArezComponentNameNotMatch
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
