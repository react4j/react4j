package com.example.prop;

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
    name = "ImmutablePropTypes",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypes extends ImmutablePropTypes {
  @Nonnull
  private final NativeComponent $$react4j$$_nativeComponent;

  React4j_ImmutablePropTypes(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    this.$$react4j$$_nativeComponent = Objects.requireNonNull( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ImmutablePropTypes" );
    }
    return componentConstructor;
  }

  @Override
  ImmutablePropTypes.MyComponent getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myProp ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myProp ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myProp ) );
    }
  }

  @Override
  String getMyOtherProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myOtherProp ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myOtherProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myOtherProp ) );
    }
  }

  @Override
  int stillAnotherProp() {
    return $$react4j$$_nativeComponent.props().getAsAny( Props.stillAnotherProp ).asInt();
  }

  @Override
  ImmutablePropTypes.KeyedComponent BobsProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.BobsProp ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.BobsProp ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.BobsProp ) );
    }
  }

  @Override
  ImmutablePropTypes.Foo getSomeProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.someProp ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.someProp ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.someProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypes) this).dispose();
  }

  @Override
  @Nullable
  protected ReactNode render() {
    assert Disposable.isNotDisposed( this );
    final ImmutablePropTypes.MyComponent $$react4jv$$_getMyProp = getMyProp();
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

    static final String myOtherProp = React.shouldMinimizePropKeys() ? "b" : "myOtherProp";

    static final String stillAnotherProp = React.shouldMinimizePropKeys() ? "c" : "stillAnotherProp";

    static final String BobsProp = React.shouldMinimizePropKeys() ? "d" : "BobsProp";

    static final String someProp = React.shouldMinimizePropKeys() ? "e" : "someProp";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_ImmutablePropTypes $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImmutablePropTypes( this );
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
    private final React4j_ImmutablePropTypes $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImmutablePropTypes( this );
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
