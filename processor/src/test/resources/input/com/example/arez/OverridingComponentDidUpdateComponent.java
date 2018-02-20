package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class OverridingComponentDidUpdateComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Action
  @Override
  protected void componentDidUpdate( @Nullable final JsPropertyMap<Object> nextProps,
                                     @Nullable final JsPropertyMap<Object> nextState )
  {
    super.componentDidUpdate( nextProps, nextState );
  }
}
