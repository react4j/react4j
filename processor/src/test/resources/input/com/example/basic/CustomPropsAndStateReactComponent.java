package com.example.basic;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class CustomPropsAndStateReactComponent
  extends Component<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State>
{

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  public static class Props
    extends BaseProps
  {
    boolean someField;
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class State
    extends BaseState
  {
    String someText;
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
