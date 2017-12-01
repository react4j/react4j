package com.example.inject;

import javax.inject.Inject;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class MethodInjectReactComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Inject
  public void setFoo( String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
