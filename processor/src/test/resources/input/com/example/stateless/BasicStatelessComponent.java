package com.example.stateless;

import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.ReactElement;
import react.core.StatelessComponent;

@ReactComponent
class BasicStatelessComponent
  extends StatelessComponent<BaseProps>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
