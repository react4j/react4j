package com.example.prop_validate;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import java.util.Objects;
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

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "CharPropValidate",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_CharPropValidate extends CharPropValidate {
  @Nonnull
  private final NativeComponent $$react4j$$_nativeComponent;

  React4j_CharPropValidate(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    this.$$react4j$$_nativeComponent = Objects.requireNonNull( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CharPropValidate" );
    }
    return componentConstructor;
  }

  @Override
  char getMyProp() {
    return $$react4j$$_nativeComponent.props().getAsAny( Props.myProp ).asChar();
  }

  private void $$react4j$$_validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( Props.myProp );
    if ( null != raw$myProp ) {
      final char typed$myProp = Js.asChar( raw$myProp );
      validateMyProp( typed$myProp );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    if ( React.shouldValidatePropValues() ) {
      $$react4j$$_validatePropValues( nextProps );
    }
    final JsPropertyMap<Object> props = $$react4j$$_nativeComponent.props();
    if ( !Js.isTripleEqual( props.get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_CharPropValidate) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_CharPropValidate $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_CharPropValidate( this );
      if ( React.shouldValidatePropValues() ) {
        assert null != props;
        $$react4j$$_component.$$react4j$$_validatePropValues( props );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.$$react4j$$_render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_CharPropValidate $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_CharPropValidate( this );
      if ( React.shouldValidatePropValues() ) {
        assert null != props;
        $$react4j$$_component.$$react4j$$_validatePropValues( props );
      }
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
      return $$react4j$$_component.$$react4j$$_render();
    }
  }
}
