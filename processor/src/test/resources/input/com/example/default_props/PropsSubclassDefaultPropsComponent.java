package com.example.default_props;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class PropsSubclassDefaultPropsComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  public static class SC
    extends BaseProps
  {
    public int myField;
  }

  @Nonnull
  static SC getInitialProps()
  {
    return new SC();
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
