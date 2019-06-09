package com.example.on_prop_change;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.InjectMode;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeComponent;

@ArezComponent(
    name = "PostUpdateOnPropChange",
    disposeNotifier = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_PostUpdateOnPropChange extends PostUpdateOnPropChange {
  React4j_PostUpdateOnPropChange(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PostUpdateOnPropChange" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.myProp ) ? props().getAsAny( Props.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.myProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    if ( null != prevProps ) {
      final JsPropertyMap<Object> props = props();
      final boolean myProp = !Js.isTripleEqual( props.get( Props.myProp ), prevProps.get( Props.myProp ) );
      if ( myProp ) {
        onMyPropChange( Js.uncheckedCast( props.getAsAny( Props.myProp ) ) );
      }
    }
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_PostUpdateOnPropChange) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class LiteNativeReactComponent extends NativeComponent {
    private React4j_PostUpdateOnPropChange $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_PostUpdateOnPropChange( this );
    }

    @JsMethod
    protected final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @JsMethod
    protected final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      $$react4j$$_component.$$react4j$$_componentDidUpdate( prevProps );
    }

    @JsMethod
    @Nullable
    protected final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent {
    private React4j_PostUpdateOnPropChange $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_PostUpdateOnPropChange( this );
    }

    @JsMethod
    protected final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @JsMethod
    protected final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      $$react4j$$_component.$$react4j$$_componentDidUpdate( prevProps );
    }

    @JsMethod
    protected final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @JsMethod
    @Nullable
    protected final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
