package com.example.optional_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class OptionalChildrenWithOptionalAndRequired
  extends Component
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Prop( require = Feature.DISABLE )
  protected abstract ReactNode[] getChildren();

  @Prop
  protected abstract String getMyProp();

  @Prop
  protected abstract String getMyRequiredProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
