package com.example.arez;

import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.Procedure;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactElement;
import react4j.core.ReactNode;

@ArezComponent(
    name = "ComponentWithEventHandler"
)
@Generated("react4j.processor.ReactProcessor")
class ComponentWithEventHandler_ extends ComponentWithEventHandler {
  private static final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private final Procedure _handleFoo = create_handleFoo();

  @Nonnull
  private final ComponentWithEventHandler.CustomHandler _handleFoo2 = create_handleFoo2();

  @Nonnull
  static ReactElement<BaseProps, NativeReactComponent> _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactElement<BaseProps, NativeReactComponent> _create(@Nullable final BaseProps props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactElement<BaseProps, NativeReactComponent> _create(@Nullable final BaseProps props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "ComponentWithEventHandler" );
    }
    return componentConstructor;
  }

  @Nonnull
  static Procedure _handleFoo(@Nonnull final ComponentWithEventHandler component) {
    return ((ComponentWithEventHandler_) component)._handleFoo;
  }

  @Nonnull
  static ComponentWithEventHandler.CustomHandler _handleFoo2(@Nonnull final ComponentWithEventHandler component) {
    return ((ComponentWithEventHandler_) component)._handleFoo2;
  }

  @Nonnull
  private Procedure create_handleFoo() {
    final Procedure handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithEventHandler.handleFoo" ) ) );
    }
    return handler;
  }

  @Nonnull
  private ComponentWithEventHandler.CustomHandler create_handleFoo2() {
    final ComponentWithEventHandler.CustomHandler handler = arg0 -> this.handleFoo2(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithEventHandler.handleFoo2" ) ) );
    }
    return handler;
  }

  @Action(
      reportParameters = false
  )
  void handleFoo() {
    super.handleFoo();
  }

  @Action(
      reportParameters = false
  )
  int handleFoo2(final int arg0) {
    return super.handleFoo2(arg0);
  }

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull BaseProps arg0, @Nonnull BaseState arg1);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull BaseProps arg0, @Nonnull BaseState arg1);
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, ComponentWithEventHandler> implements Lifecycle {
    NativeReactComponent(@Nonnull final BaseProps props) {
      super( props );
    }

    @Override
    protected ComponentWithEventHandler createComponent() {
      return new Arez_ComponentWithEventHandler_();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
      performComponentDidUpdate(arg0,arg1);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
      return performShouldComponentUpdate(arg0,arg1);
    }
  }
}
