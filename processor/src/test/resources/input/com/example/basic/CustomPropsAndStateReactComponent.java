package com.example.basic;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.State;

@ReactComponent
abstract class CustomPropsAndStateReactComponent
  extends Component
{
  @Prop
  abstract boolean isSomeField();

  @State
  abstract String someText();

  @State
  abstract void setSomeText( String value );

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
