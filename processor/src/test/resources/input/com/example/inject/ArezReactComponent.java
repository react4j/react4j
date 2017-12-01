package com.example.inject;

import javax.annotation.Nullable;
import javax.inject.Inject;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactNode;

@ReactComponent
class ArezReactComponent
  extends ReactArezComponent<BaseProps, BaseState, BaseContext>
{
  @Inject
  String someParam;

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
