package com.example.mixins;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class AssembledComponent
  extends Component
  implements MyMixin
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
