package com.example.prop;

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

@Generated("react4j.processor.ReactProcessor")
class React4j_NullablePropAndNonnullChildComponent extends NullablePropAndNonnullChildComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NullablePropAndNonnullChildComponent" );
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

  @Override
  protected ReactNode getChild() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.child ) ? props().getAny( Props.child ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.child ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";

    static final String myProp2 = React.shouldMinimizePropKeys() ? "b" : "myProp2";

    static final String child = "children";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NullablePropAndNonnullChildComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NullablePropAndNonnullChildComponent createComponent() {
      return new React4j_NullablePropAndNonnullChildComponent();
    }
  }
}
