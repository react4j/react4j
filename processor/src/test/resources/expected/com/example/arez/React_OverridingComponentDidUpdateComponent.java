package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@JsType
@Generated("react4j.processor.ReactProcessor")
final class React_OverridingComponentDidUpdateComponent extends NativeAdapterComponent<BaseProps, BaseState, OverridingComponentDidUpdateComponent> {
  @JsConstructor
  private React_OverridingComponentDidUpdateComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected OverridingComponentDidUpdateComponent createComponent() {
    return new Arez_OverridingComponentDidUpdateComponent();
  }

  public void componentDidMount() {
    performComponentDidMount();
  }

  public void componentDidUpdate(@Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState) {
    performComponentDidUpdate(nextProps,nextState);
  }

  public void componentWillReceiveProps(@Nonnull final BaseProps arg0) {
    performComponentWillReceiveProps(arg0);
  }

  public void componentWillUnmount() {
    performComponentWillUnmount();
  }

  public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    return performShouldComponentUpdate(arg0,arg1);
  }
}
