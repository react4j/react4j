package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactNode;

@ReactComponent
abstract class OverridingComponentDidUpdateComponent
  extends ReactArezComponent<BaseProps, BaseContext>
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Action
  @Override
  protected void componentDidUpdate( @Nullable final BaseProps nextProps, @Nullable final BaseState nextState )
  {
    super.componentDidUpdate( nextProps, nextState );
  }
}
