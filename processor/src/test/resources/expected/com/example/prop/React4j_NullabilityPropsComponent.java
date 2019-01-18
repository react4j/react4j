package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ReactConfig;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_NullabilityPropsComponent extends NullabilityPropsComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NullabilityPropsComponent" );
    }
    return componentConstructor;
  }

  @Nonnull
  @Override
  protected String getMyProp() {
    return props().getAny( Props.myProp ).asString();
  }

  @Nullable
  @Override
  protected String getMyProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp2 ) ? props().getAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp2 ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp";

    static final String myProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myProp2";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NullabilityPropsComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NullabilityPropsComponent createComponent() {
      return new React4j_NullabilityPropsComponent();
    }
  }
}
