package com.example.lifecycle;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
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

  @PreUpdate
  final void preUpdate()
  {
  }

  @PostUpdate
  final void postUpdate()
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
}
