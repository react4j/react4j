package com.example.arez;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.Autorun;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactNode;

@ReactComponent
class AutorunArezReactComponent
  extends ReactArezComponent<BaseProps, BaseState, BaseContext>
{
  @Autorun
  public void myAutorun()
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
