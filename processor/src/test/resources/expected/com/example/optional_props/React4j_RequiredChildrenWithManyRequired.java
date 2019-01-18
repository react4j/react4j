package com.example.optional_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ReactConfig;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
class React4j_RequiredChildrenWithManyRequired extends RequiredChildrenWithManyRequired {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyRequired" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp1() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myRequiredProp1 ) ? props().getAny( Props.myRequiredProp1 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp1 ) );
    }
  }

  @Override
  protected String getMyRequiredProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myRequiredProp2 ) ? props().getAny( Props.myRequiredProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp2 ) );
    }
  }

  @Override
  protected String getMyRequiredProp3() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myRequiredProp3 ) ? props().getAny( Props.myRequiredProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp3 ) );
    }
  }

  @Override
  protected ReactNode[] getChildren() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.children ) ? props().getAny( Props.children ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.children ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myRequiredProp1 = ReactConfig.shouldMinimizePropKeys() ? "a" : "myRequiredProp1";

    static final String myRequiredProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myRequiredProp2";

    static final String myRequiredProp3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "myRequiredProp3";

    static final String children = "children";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RequiredChildrenWithManyRequired> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RequiredChildrenWithManyRequired createComponent() {
      return new React4j_RequiredChildrenWithManyRequired();
    }
  }
}
