package com.example.on_prop_changed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadNullability2OnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( @Nullable String oldValue )
  {
  }

  @Nonnull
  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
