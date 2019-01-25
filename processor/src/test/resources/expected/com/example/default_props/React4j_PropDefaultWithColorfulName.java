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
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentWillUnmount;

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
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
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

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_PropDefaultWithColorfulName) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp12$23 = React.shouldMinimizePropKeys() ? "a" : "myProp12$23";
  }

  private static final class LiteNativeReactComponent extends NativeComponent {
    private React4j_PropDefaultWithColorfulName $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_PropDefaultWithColorfulName( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnComponentWillUnmount {
    private React4j_PropDefaultWithColorfulName $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_PropDefaultWithColorfulName( this );
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
