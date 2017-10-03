package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
final class React_OverridingComponentDidUpdateComponent extends NativeAdapterComponent<BaseProps, BaseState, OverridingComponentDidUpdateComponent> {
  @JsConstructor
  private React_OverridingComponentDidUpdateComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected OverridingComponentDidUpdateComponent createComponent() {
    return new Arez_OverridingComponentDidUpdateComponent();
  }

  public void componentWillUnmount() {
    performComponentWillUnmount();
  }

  public void componentWillReceiveProps(@Nonnull final BaseProps arg0) {
    performComponentWillReceiveProps(arg0);
  }

  public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    return performShouldComponentUpdate(arg0,arg1);
  }
}
