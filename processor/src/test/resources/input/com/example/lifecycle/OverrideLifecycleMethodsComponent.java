package com.example.lifecycle;

import javax.annotation.Nonnull;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;
import react.core.StatelessComponent;
import static org.realityforge.braincheck.Guards.fail;

@ReactComponent
class OverrideLifecycleMethodsComponent
  extends Component<BaseProps,BaseState>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @Override
  protected final void componentDidUpdate( @Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState )
  {
  }

  @Override
  protected void componentWillMount()
  {
  }

  @Override
  protected void componentWillReceiveProps( @Nonnull final BaseProps nextProps )
  {
  }

  @Override
  protected void componentWillUnmount()
  {
  }

  @Override
  protected void componentWillUpdate( @Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState )
  {
  }

  @Override
  public boolean shouldComponentUpdate( @Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState )
  {
    return super.shouldComponentUpdate( nextProps, nextState );
  }
}
