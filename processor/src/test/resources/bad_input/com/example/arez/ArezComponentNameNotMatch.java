package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.ArezComponent;
import react.annotations.ReactComponent;
import react.arez.ReactArezComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;

@ReactComponent
@ArezComponent( name = "X" )
class ArezComponentNameNotMatch
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    return null;
  }
}
