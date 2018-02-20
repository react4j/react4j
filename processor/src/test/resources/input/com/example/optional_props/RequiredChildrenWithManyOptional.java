package com.example.optional_props;

import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

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
  protected abstract ReactNode[] getChildren();

  @Prop
  protected abstract String getMyPropA();

  @Prop
  protected abstract String getMyPropB();

  @Prop
  protected abstract String getMyPropC();

  @Prop
  protected abstract String getMyPropD();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
