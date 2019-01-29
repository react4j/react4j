package com.example.optional_props;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.InjectMode;
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
import react4j.internal.OnShouldComponentUpdate;

@ArezComponent(
    name = "ExplicitOptional",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE,
    dagger = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ExplicitOptional extends ExplicitOptional {
  React4j_ExplicitOptional(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
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

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.myRequiredProp ), nextProps.get( Props.myRequiredProp ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myOptionalProp ), nextProps.get( Props.myOptionalProp ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myOtherOptionalProp ), nextProps.get( Props.myOtherOptionalProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ExplicitOptional) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myRequiredProp = React.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

    static final String myOptionalProp = React.shouldMinimizePropKeys() ? "b" : "myOptionalProp";

    static final String myOtherOptionalProp = React.shouldMinimizePropKeys() ? "c" : "myOtherOptionalProp";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate {
    private React4j_ExplicitOptional $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ExplicitOptional( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private React4j_ExplicitOptional $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ExplicitOptional( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
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
