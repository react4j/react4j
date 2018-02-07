package com.example.default_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class PropsSubclassDefaultPropsComponent_ extends PropsSubclassDefaultPropsComponent {
  static final ComponentConstructorFunction<BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropsSubclassDefaultPropsComponent" );
    }
    Js.asPropertyMap( componentConstructor ).set( "defaultProps", PropsSubclassDefaultPropsComponent.getInitialProps() );
    return componentConstructor;
  }

  @Override
  int isMyField() {
    return Js.asPropertyMap( props() ).getAny( "isMyField" ).asInt();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, BaseContext, PropsSubclassDefaultPropsComponent> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected PropsSubclassDefaultPropsComponent createComponent() {
      return new PropsSubclassDefaultPropsComponent_();
    }
  }
}
