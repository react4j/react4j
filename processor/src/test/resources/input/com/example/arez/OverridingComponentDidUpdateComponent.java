package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;
import react.annotations.ReactComponent;
import react.arez.ReactArezComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;

@ReactComponent
@ArezComponent
class OverridingComponentDidUpdateComponent
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
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
