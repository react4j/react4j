package com.example.context;

import javax.annotation.Nullable;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.annotations.ReactComponent;
import react4j.core.BaseChildContext;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BasicProviderComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class Context
    extends BaseChildContext
  {
    String newTodo;
  }

  @Nullable
  @Override
  protected Context getChildContext()
  {
    return null;
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
