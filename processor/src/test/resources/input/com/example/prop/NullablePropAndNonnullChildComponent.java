package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NullablePropAndNonnullChildComponent
  extends Component
{
  @Prop
  @Nonnull
  protected abstract String getMyProp();

  @Prop
  @Nullable
  protected abstract String getMyProp2();

  @Prop
  protected abstract ReactNode getChild();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
