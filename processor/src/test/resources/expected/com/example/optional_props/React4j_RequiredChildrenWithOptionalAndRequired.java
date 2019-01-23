package com.example.optional_props;

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
    name = "RequiredChildrenWithOptionalAndRequired",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_RequiredChildrenWithOptionalAndRequired extends RequiredChildrenWithOptionalAndRequired {
  React4j_RequiredChildrenWithOptionalAndRequired(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithOptionalAndRequired" );
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
  protected String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp ) ? props().getAny( Props.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp ) );
    }
  }

  @Override
  protected ReactNode[] getChildren() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.children ) ? props().getAny( Props.children ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.children ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myRequiredProp = React.shouldMinimizePropKeys() ? "a" : "myRequiredProp";

    static final String myProp = React.shouldMinimizePropKeys() ? "b" : "myProp";

    static final String children = "children";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithOptionalAndRequired> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithOptionalAndRequired createComponent() {
      return new Arez_React4j_RequiredChildrenWithOptionalAndRequired( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_RequiredChildrenWithOptionalAndRequired) component() ).render();
    }
  }
}
