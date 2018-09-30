package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_CustomPropsAndStateReactComponent extends CustomPropsAndStateReactComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomPropsAndStateReactComponent" );
    }
    return componentConstructor;
  }

  @Override
  boolean isSomeField() {
    return Js.<Props>uncheckedCast( props() ).someField;
  }

  @Override
  String someText() {
    return state().getAny( "someText" ).asString();
  }

  @Override
  void setSomeText(final String value) {
    scheduleStateUpdate( ( ( previousState, currentProps ) -> JsPropertyMap.of( "someText", value ) ) );
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "Object"
  )
  static final class Props {
    @Nullable
    Object key;

    boolean someField;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<CustomPropsAndStateReactComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected CustomPropsAndStateReactComponent createComponent() {
      return new React4j_CustomPropsAndStateReactComponent();
    }
  }
}
