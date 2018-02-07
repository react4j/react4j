package com.example.context;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BasicContextComponent
  extends Component<BaseState, BasicContextComponent.Context>
{
  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class Context
    extends BaseContext
  {
    String newTodo;
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
