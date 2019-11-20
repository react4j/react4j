package com.example.optional_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class RequiredChildrenWithManyOptional
  extends Component
{
  @PropDefault
  static final String DEFAULT_MY_PROP_A = "Foo";
  @PropDefault
  static final String DEFAULT_MY_PROP_B = "Foo";
  @PropDefault
  static final String DEFAULT_MY_PROP_C = "Foo";
  @PropDefault
  static final String DEFAULT_MY_PROP_D = "Foo";

  @Prop
  abstract ReactNode[] getChildren();

  @Prop
  abstract String getMyPropA();

  @Prop
  abstract String getMyPropB();

  @Prop
  abstract String getMyPropC();

  @Prop
  abstract String getMyPropD();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
