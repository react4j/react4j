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
import react4j.internal.NativeComponent;

@ArezComponent(
    name = "PropTypeBoolean",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_PropTypeBoolean extends PropTypeBoolean {
  React4j_PropTypeBoolean(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropTypeBoolean" );
    }
    return componentConstructor;
  }

  @Override
  protected boolean getMyProp() {
    return props().getAny( Props.myProp ).asBoolean();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class NativeReactComponent extends NativeComponent {
    private React4j_PropTypeBoolean $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_PropTypeBoolean( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
