package com.example.default_props;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DefaultFieldContextPropModel
  extends Component
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Prop( source = Prop.Source.CONTEXT )
  abstract String getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
