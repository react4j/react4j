package com.example.lifecycle;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsType;
import react4j.core.BaseProps;
import react4j.core.BaseState;

@Generated("react4j.processor.ReactProcessor")
@JsType(
    isNative = true
)
interface OverrideLifecycleMethodsComponentLifecycle {
  void componentDidUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);

  void componentWillMount();

  void componentWillReceiveProps(@Nonnull BaseProps nextProps);

  void componentWillUnmount();

  void componentWillUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);

  boolean shouldComponentUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);
}
