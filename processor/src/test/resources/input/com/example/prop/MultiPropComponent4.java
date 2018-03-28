package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

/**
 * This prop has multiple optional props
 */
@ReactComponent
abstract class MultiPropComponent4
  extends Component
{
  @Prop
  protected abstract String getMyProp();

  @Prop
  @Nullable
  protected abstract String getMyProp2();

  @Prop
  @Nullable
  protected abstract String getMyProp3();

  @Prop
  @Nullable
  protected abstract String getMyProp4();

  @Prop
  @Nullable
  protected abstract ReactNode getChild();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
