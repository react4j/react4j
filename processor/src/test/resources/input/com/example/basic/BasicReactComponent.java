package com.example.basic;

import javax.annotation.Nonnull;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;
import react.core.SideComponent;

@ReactComponent
class BasicReactComponent
  extends SideComponent<BaseProps, BaseState>
{
  BasicReactComponent( @Nonnull final Component<BaseProps, BaseState> component )
  {
    super( component );
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
