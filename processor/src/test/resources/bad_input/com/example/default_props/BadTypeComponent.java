package com.example.default_props;

import javax.annotation.Nonnull;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BadTypeComponent
  extends Component<BaseState, BaseContext>
{
  @Nonnull
  static String getInitialProps()
  {
    return "";
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
