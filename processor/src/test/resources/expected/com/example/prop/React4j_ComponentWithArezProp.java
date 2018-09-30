package com.example.prop;

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
class React4j_ComponentWithArezProp extends ComponentWithArezProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentWithArezProp" );
    }
    return componentConstructor;
  }

  @Override
  protected String getValue() {
    return Js.<Props>uncheckedCast( props() ).value;
  }

  @Override
  protected ComponentWithArezProp.Model getModel() {
    return Js.<Props>uncheckedCast( props() ).model;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "Object"
  )
  static final class Props {
    @Nullable
    Object key;

    String value;

    ComponentWithArezProp.Model model;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ComponentWithArezProp> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentWithArezProp createComponent() {
      return new React4j_ComponentWithArezProp();
    }
  }
}
