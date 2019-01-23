package com.example.default_props;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;

@ArezComponent(
    name = "PropDefaultWithColorfulName",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_PropDefaultWithColorfulName extends PropDefaultWithColorfulName {
  React4j_PropDefaultWithColorfulName(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropDefaultWithColorfulName" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp12$23() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp12$23 ) ? props().getAny( Props.myProp12$23 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp12$23 ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp12$23 = React.shouldMinimizePropKeys() ? "a" : "myProp12$23";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PropDefaultWithColorfulName> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropDefaultWithColorfulName createComponent() {
      return new Arez_React4j_PropDefaultWithColorfulName( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_PropDefaultWithColorfulName) component() ).render();
    }
  }
}
