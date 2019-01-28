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

@ArezComponent(
    name = "ImmutablePropTypes",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE,
    dagger = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ImmutablePropTypes extends ImmutablePropTypes {
  React4j_ImmutablePropTypes(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
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
  protected ImmutablePropTypes.MyComponent getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp ) ? props().getAny( Props.myProp ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp ) );
    }
  }

  @Override
  protected String getMyOtherProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myOtherProp ) ? props().getAny( Props.myOtherProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myOtherProp ) );
    }
  }

  @Override
  protected int stillAnotherProp() {
    return props().getAny( Props.stillAnotherProp ).asInt();
  }

  @Override
  protected ImmutablePropTypes.KeyedComponent BobsProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.BobsProp ) ? props().getAny( Props.BobsProp ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.BobsProp ) );
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypes) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";

    static final String myOtherProp = React.shouldMinimizePropKeys() ? "b" : "myOtherProp";

    static final String stillAnotherProp = React.shouldMinimizePropKeys() ? "c" : "stillAnotherProp";

    static final String BobsProp = React.shouldMinimizePropKeys() ? "d" : "BobsProp";
  }

  private static final class LiteNativeReactComponent extends NativeComponent {
    private React4j_ImmutablePropTypes $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImmutablePropTypes( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnComponentWillUnmount {
    private React4j_ImmutablePropTypes $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImmutablePropTypes( this );
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
