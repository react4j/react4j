package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactElement;

@ReactComponent
@ArezComponent
class OverridingComponentDidUpdateComponent
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected ReactElement<?, ?> render()
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
