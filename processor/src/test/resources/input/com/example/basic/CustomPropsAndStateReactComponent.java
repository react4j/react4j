package com.example.basic;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class CustomPropsAndStateReactComponent
  extends Component<CustomPropsAndStateReactComponent.State, BaseContext>
{
  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class State
    extends BaseState
  {
    String someText;
  }

  @Prop
  abstract boolean isSomeField();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
