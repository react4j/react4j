package com.example.default_props;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DefaultMethodContextPropModel
  extends Component
{
  @Prop( source = Prop.Source.CONTEXT )
  abstract String getMyProp();

  @PropDefault
  public static String getMyPropDefault()
  {
    return "Foo";
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
