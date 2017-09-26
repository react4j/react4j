package com.example.component;

import javax.annotation.Nonnull;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;
import react.core.SideComponent;

@ReactComponent
final class FinalComponent
  extends SideComponent<BaseProps, BaseState>
{
  FinalComponent( @Nonnull final Component<BaseProps, BaseState> component )
  {
    super( component );
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
