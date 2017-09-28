package com.example.basic;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

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
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
