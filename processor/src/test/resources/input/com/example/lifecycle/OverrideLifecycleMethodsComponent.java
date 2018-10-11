package com.example.lifecycle;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.Component;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class OverrideLifecycleMethodsComponent
  extends Component
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
  protected final void componentDidUpdate( @Nullable final JsPropertyMap<Object> nextProps )
  {
  }

  @Override
  protected void componentWillUnmount()
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
  protected boolean shouldComponentUpdate( @Nullable final JsPropertyMap<Object> nextProps )
  {
    return super.shouldComponentUpdate( nextProps );
  }
}
