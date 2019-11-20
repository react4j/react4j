package com.example.default_props;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ProtectedFieldPropDefault
  extends Component
{
  @Nonnull
  @PropDefault
  protected static final String DEFAULT_MY_PROP = "Foo";

  @Prop
  abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
