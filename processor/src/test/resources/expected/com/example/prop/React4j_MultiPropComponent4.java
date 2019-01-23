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
    name = "MultiPropComponent4",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_MultiPropComponent4 extends MultiPropComponent4 {
  React4j_MultiPropComponent4(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent4" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp ) ? props().getAny( Props.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp ) );
    }
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

  @Nullable
  @Override
  protected String getMyProp3() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp3 ) ? props().getAny( Props.myProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp3 ) );
    }
  }

  @Nullable
  @Override
  protected String getMyProp4() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp4 ) ? props().getAny( Props.myProp4 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp4 ) );
    }
  }

  @Nullable
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

    static final String myProp3 = React.shouldMinimizePropKeys() ? "c" : "myProp3";

    static final String myProp4 = React.shouldMinimizePropKeys() ? "d" : "myProp4";

    static final String child = "children";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultiPropComponent4> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultiPropComponent4 createComponent() {
      return new Arez_React4j_MultiPropComponent4( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_MultiPropComponent4) component() ).render();
    }
  }
}
