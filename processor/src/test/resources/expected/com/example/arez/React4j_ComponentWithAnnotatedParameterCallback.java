package com.example.arez;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import elemental2.core.JsObject;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@ArezComponent(
    name = "ComponentWithAnnotatedParameterCallback"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ComponentWithAnnotatedParameterCallback extends ComponentWithAnnotatedParameterCallback {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  final ComponentWithAnnotatedParameterCallback.CustomHandler2 _handleFoo = create_handleFoo();

  @Nonnull
  final ComponentWithAnnotatedParameterCallback.CustomHandler _handleFoo2 = create_handleFoo2();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithAnnotatedParameterCallback" );
    }
    return componentConstructor;
  }

  @Override
  @Action
  protected void reportPropsChanged(@Nullable final JsPropertyMap<Object> nextProps) {
  }

  @Nonnull
  private ComponentWithAnnotatedParameterCallback.CustomHandler2 create_handleFoo() {
    final ComponentWithAnnotatedParameterCallback.CustomHandler2 handler = () -> this.handleFoo();
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithAnnotatedParameterCallback.handleFoo" ) ) );
    }
    return handler;
  }

  @Nonnull
  private ComponentWithAnnotatedParameterCallback.CustomHandler create_handleFoo2() {
    final ComponentWithAnnotatedParameterCallback.CustomHandler handler = arg0 -> this.handleFoo2(arg0);
    if( ReactConfig.enableComponentNames() ) {
      JsObject.defineProperty( Js.cast( handler ), "name", Js.cast( JsPropertyMap.of( "value", "ComponentWithAnnotatedParameterCallback.handleFoo2" ) ) );
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

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> arg0,
        @Nonnull JsPropertyMap<Object> arg1);
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ComponentWithAnnotatedParameterCallback> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithAnnotatedParameterCallback createComponent() {
      return new Arez_React4j_ComponentWithAnnotatedParameterCallback();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final JsPropertyMap<Object> arg1) {
      performComponentDidUpdate(arg0,arg1);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> arg0,
        @Nonnull final JsPropertyMap<Object> arg1) {
      return performShouldComponentUpdate(arg0,arg1);
    }
  }
}
