package com.example.basic;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CustomPropsReactComponent
  extends Component
{
  @Prop
  abstract boolean isSomeField();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
