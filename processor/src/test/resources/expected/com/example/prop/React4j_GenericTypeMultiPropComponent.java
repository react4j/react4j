package com.example.prop;

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
    name = "GenericTypeMultiPropComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_GenericTypeMultiPropComponent<T> extends GenericTypeMultiPropComponent<T> {
  React4j_GenericTypeMultiPropComponent(
      @Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericTypeMultiPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected T getValue() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value ) ? props().getAny( Props.value ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value ) );
    }
  }

  @Override
  protected String getValue2() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value2 ) ? props().getAny( Props.value2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value2 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue3() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value3 ) ? props().getAny( Props.value3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value3 ) );
    }
  }

  @Nullable
  @Override
  protected String getValue4() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value4 ) ? props().getAny( Props.value4 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value4 ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.value ), nextProps.get( Props.value ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.value2 ), nextProps.get( Props.value2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.value3 ), nextProps.get( Props.value3 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.value4 ), nextProps.get( Props.value4 ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_GenericTypeMultiPropComponent) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String value = React.shouldMinimizePropKeys() ? "a" : "value";

    static final String value2 = React.shouldMinimizePropKeys() ? "b" : "value2";

    static final String value3 = React.shouldMinimizePropKeys() ? "c" : "value3";

    static final String value4 = React.shouldMinimizePropKeys() ? "d" : "value4";
  }

  private static final class LiteNativeReactComponent<T> extends NativeComponent implements OnShouldComponentUpdate {
    private React4j_GenericTypeMultiPropComponent<T> $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_GenericTypeMultiPropComponent<T>( this );
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

  private static final class NativeReactComponent<T> extends NativeComponent implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private React4j_GenericTypeMultiPropComponent<T> $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_GenericTypeMultiPropComponent<T>( this );
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
