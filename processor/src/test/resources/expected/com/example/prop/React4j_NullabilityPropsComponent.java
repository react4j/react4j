package com.example.prop;

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
    name = "NullabilityPropsComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_NullabilityPropsComponent extends NullabilityPropsComponent {
  React4j_NullabilityPropsComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
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
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp2 ) ? props().getAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp2 ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";

    static final String myProp2 = React.shouldMinimizePropKeys() ? "b" : "myProp2";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NullabilityPropsComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NullabilityPropsComponent createComponent() {
      return new Arez_React4j_NullabilityPropsComponent( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_NullabilityPropsComponent) component() ).render();
    }
  }
}
