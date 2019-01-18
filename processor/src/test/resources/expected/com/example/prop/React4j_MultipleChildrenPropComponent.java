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
class React4j_MultipleChildrenPropComponent extends MultipleChildrenPropComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultipleChildrenPropComponent" );
    }
    return componentConstructor;
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
    static final String children = "children";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultipleChildrenPropComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultipleChildrenPropComponent createComponent() {
      return new React4j_MultipleChildrenPropComponent();
    }
  }
}
