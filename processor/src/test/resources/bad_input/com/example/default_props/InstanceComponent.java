package com.example.default_props;

import javax.annotation.Nonnull;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class InstanceComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Nonnull
  BaseProps getInitialProps()
  {
    return new BaseProps();
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
