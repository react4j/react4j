package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.RenderElement;

@ReactComponent
@ArezComponent( allowEmpty = true )
class ArezComponentAnnotated
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected RenderElement render()
  {
    return null;
  }
}
