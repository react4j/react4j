package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.Action;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactNode;

@ReactComponent
class OverridingComponentDidUpdateComponent
  extends ReactArezComponent<BaseProps, BaseState>
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
