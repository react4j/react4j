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
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@ArezComponent(
    type = "ComponentWithAnnotatedParameterEventHandler"
)
@Generated("react4j.processor.ReactProcessor")
class ComponentWithAnnotatedParameterEventHandler_ extends ComponentWithAnnotatedParameterEventHandler {
  private static final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private final ComponentWithAnnotatedParameterEventHandler.CustomHandler2 _handleFoo = create_handleFoo();

  @Nonnull
  private final ComponentWithAnnotatedParameterEventHandler.CustomHandler _handleFoo2 = create_handleFoo2();

  @Nonnull
  static ReactNode _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "ComponentWithAnnotatedParameterEventHandler" );
    }
    return componentConstructor;
  }

  @Nonnull
  static ComponentWithAnnotatedParameterEventHandler.CustomHandler2 _handleFoo(@Nonnull final ComponentWithAnnotatedParameterEventHandler component) {
    return ((ComponentWithAnnotatedParameterEventHandler_) component)._handleFoo;
  }

  @Nonnull
  static ComponentWithAnnotatedParameterEventHandler.CustomHandler _handleFoo2(@Nonnull final ComponentWithAnnotatedParameterEventHandler component) {
    return ((ComponentWithAnnotatedParameterEventHandler_) component)._handleFoo2;
  }

  @Nonnull
  private ComponentWithAnnotatedParameterEventHandler.CustomHandler2 create_handleFoo() {
    final ComponentWithAnnotatedParameterEventHandler.CustomHandler2 handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithAnnotatedParameterEventHandler.handleFoo" ) ) );
    }
    return handler;
  }

  @Nonnull
  private ComponentWithAnnotatedParameterEventHandler.CustomHandler create_handleFoo2() {
    final ComponentWithAnnotatedParameterEventHandler.CustomHandler handler = arg0 -> this.handleFoo2(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithAnnotatedParameterEventHandler.handleFoo2" ) ) );
    }
    return handler;
  }

  @Nonnull
  @Action(
      reportParameters = false
  )
  String handleFoo() {
    return super.handleFoo();
  }

  @Action(
      reportParameters = false
  )
  void handleFoo2(@Nonnull final String arg0) {
    super.handleFoo2(arg0);
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

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, ComponentWithAnnotatedParameterEventHandler> implements Lifecycle {
    NativeReactComponent(@Nonnull final BaseProps props) {
      super( props );
    }

    @Override
    protected ComponentWithAnnotatedParameterEventHandler createComponent() {
      return new Arez_ComponentWithAnnotatedParameterEventHandler_();
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
