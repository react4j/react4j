package com.example.mixins;

import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class AssembledComponent
  extends Component<BaseState>
  implements MyMixin
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
