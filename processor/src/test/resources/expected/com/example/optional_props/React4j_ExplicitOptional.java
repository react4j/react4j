package com.example.optional_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_ExplicitOptional extends ExplicitOptional {
  React4j_ExplicitOptional(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ExplicitOptional" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myRequiredProp ) ? props().getAny( Props.myRequiredProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp ) );
    }
  }

  @Override
  protected String getMyOptionalProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myOptionalProp ) ? props().getAny( Props.myOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myOptionalProp ) );
    }
  }

  @Override
  protected String getMyOtherOptionalProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myOtherOptionalProp ) ? props().getAny( Props.myOtherOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myOtherOptionalProp ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myRequiredProp = React.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

    static final String myOptionalProp = React.shouldMinimizePropKeys() ? "b" : "myOptionalProp";

    static final String myOtherOptionalProp = React.shouldMinimizePropKeys() ? "c" : "myOtherOptionalProp";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ExplicitOptional> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ExplicitOptional createComponent() {
      return new React4j_ExplicitOptional( this );
    }
  }
}
