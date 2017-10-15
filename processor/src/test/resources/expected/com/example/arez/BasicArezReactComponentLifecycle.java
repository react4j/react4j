package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsType;
import react4j.core.BaseProps;
import react4j.core.BaseState;

@Generated("react4j.processor.ReactProcessor")
@JsType(
    isNative = true
)
interface BasicArezReactComponentLifecycle {
  void componentDidMount();

  void componentDidUpdate(@Nonnull BaseProps arg0, @Nonnull BaseState arg1);

  void componentWillUnmount();

  boolean shouldComponentUpdate(@Nonnull BaseProps arg0, @Nonnull BaseState arg1);
}
