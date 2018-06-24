package com.example.default_props;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsMethodPropDefault
  extends Component
{
  @PropDefault
  static String getMyPropDefault()
    throws IOException
  {
    return "Foo";
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
