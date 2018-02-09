package com.example.lifecycle;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactErrorInfo;
import react4j.core.ReactNode;

@ReactComponent
abstract class OverrideLifecycleMethodsComponent
  extends Component<BaseState>
{
  // This lifecycle method should not have adapter synthesized.
  @Override
  protected void postConstruct()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Override
  protected final void componentDidUpdate( @Nullable final JsPropertyMap<Object> nextProps,
                                           @Nullable final BaseState nextState )
  {
  }

  @Override
  protected void componentWillReceiveProps( @Nonnull final JsPropertyMap<Object> nextProps )
  {
  }

  @Override
  protected void componentWillUnmount()
  {
  }

  @Override
  protected void componentWillUpdate( @Nullable final JsPropertyMap<Object> nextProps,
                                      @Nullable final BaseState nextState )
  {
  }

  @Override
  protected void componentDidMount()
  {
  }

  @Override
  protected void componentDidCatch( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }

  @Override
  protected boolean shouldComponentUpdate( @Nullable final JsPropertyMap<Object> nextProps,
                                           @Nullable final BaseState nextState )
  {
    return super.shouldComponentUpdate( nextProps, nextState );
  }
}
