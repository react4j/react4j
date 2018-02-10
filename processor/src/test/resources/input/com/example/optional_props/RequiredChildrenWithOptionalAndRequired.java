package com.example.optional_props;

import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class RequiredChildrenWithOptionalAndRequired
  extends Component<BaseState>
{
  @PropDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Prop
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
