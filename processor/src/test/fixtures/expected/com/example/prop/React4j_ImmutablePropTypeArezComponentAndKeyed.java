package com.example.prop;

import arez.Disposable;
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

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "ImmutablePropTypeArezComponentAndKeyed",
    disposeNotifier = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ImmutablePropTypeArezComponentAndKeyed extends ImmutablePropTypeArezComponentAndKeyed {
  React4j_ImmutablePropTypeArezComponentAndKeyed(
      @Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ImmutablePropTypeArezComponentAndKeyed" );
    }
    return componentConstructor;
  }

  @Override
  ImmutablePropTypeArezComponentAndKeyed.MyComponent getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.myProp ) ? props().getAsAny( Props.myProp ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.myProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypeArezComponentAndKeyed) this).dispose();
  }

  @Override
  @Nullable
  protected ReactNode render() {
    assert Disposable.isNotDisposed( this );
    final ImmutablePropTypeArezComponentAndKeyed.MyComponent $$react4jv$$_getMyProp = getMyProp();
    if ( Disposable.isDisposed( $$react4jv$$_getMyProp ) ) {
      return null;
    }
    return super.render();
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
    private final React4j_ImmutablePropTypeArezComponentAndKeyed $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImmutablePropTypeArezComponentAndKeyed( this );
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
    @Nonnull
    private final React4j_ImmutablePropTypeArezComponentAndKeyed $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImmutablePropTypeArezComponentAndKeyed( this );
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
