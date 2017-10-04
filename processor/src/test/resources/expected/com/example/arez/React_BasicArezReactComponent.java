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
final class React_BasicArezReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicArezReactComponent> {
  @JsConstructor
  private React_BasicArezReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected BasicArezReactComponent createComponent() {
    return new Arez_BasicArezReactComponent();
  }

  public void componentDidMount() {
    performComponentDidMount();
  }

  public void componentDidUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    performComponentDidUpdate(arg0,arg1);
  }

  public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    return performShouldComponentUpdate(arg0,arg1);
  }
}
