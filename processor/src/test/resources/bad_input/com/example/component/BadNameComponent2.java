package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent( name = "for" )
abstract class BadNameComponent2
  extends Component<BaseState>
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
