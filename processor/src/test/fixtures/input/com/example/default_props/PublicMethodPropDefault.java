package com.example.default_props;

import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PublicMethodPropDefault
  extends Component
{
  @Nonnull
  @PropDefault
  public static String getMyPropDefault()
  {
    return "Foo";
  }

  @Prop
  abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
