package com.example.context;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import jsinterop.base.JsPropertyMapOfAny;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.PropType;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class BasicContextComponent_ extends BasicContextComponent {
  private static final ComponentConstructorFunction<BaseProps, BasicContextComponent.Context> TYPE = getConstructorFunction();

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
  private static ComponentConstructorFunction<BaseProps, BasicContextComponent.Context> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BasicContextComponent.Context> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "BasicContextComponent" );
    }
    final PropType valid = () -> null;
    final JsPropertyMapOfAny contextTypes = JsPropertyMap.of();
    contextTypes.set( "newTodo", valid );
    JsPropertyMap.of( componentConstructor ).set( "contextTypes", contextTypes );
    return componentConstructor;
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicContextComponent.Context, BasicContextComponent> {
    NativeReactComponent(@Nullable final BaseProps props,
        @Nullable final BasicContextComponent.Context context) {
      super( props, context );
    }

    @Override
    protected BasicContextComponent createComponent() {
      return new BasicContextComponent_();
    }
  }
}
