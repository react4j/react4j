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
    name = "CustomNamingOnPropChange",
    disposeNotifier = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_CustomNamingOnPropChange extends CustomNamingOnPropChange {
  React4j_CustomNamingOnPropChange(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "CustomNamingOnPropChange" );
    }
    return componentConstructor;
  }

  @Override
  protected boolean getMyProp1() {
    return props().getAsAny( Props.myProp1 ).asBoolean();
  }

  @Override
  protected String getMyProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.myProp2 ) ? props().getAsAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.myProp2 ) );
    }
  }

  @Override
  protected int getMyProp3() {
    return props().getAsAny( Props.myProp3 ).asInt();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.myProp1 ), nextProps.get( Props.myProp1 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myProp2 ), nextProps.get( Props.myProp2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myProp3 ), nextProps.get( Props.myProp3 ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    if ( null != prevProps ) {
      final JsPropertyMap<Object> props = props();
      final boolean myProp1 = !Js.isTripleEqual( props.get( Props.myProp1 ), prevProps.get( Props.myProp1 ) );
      final boolean myProp2 = !Js.isTripleEqual( props.get( Props.myProp2 ), prevProps.get( Props.myProp2 ) );
      final boolean myProp3 = !Js.isTripleEqual( props.get( Props.myProp3 ), prevProps.get( Props.myProp3 ) );
      if ( myProp1 ) {
        onPropChange( props.getAsAny( Props.myProp1 ).asBoolean() );
      }
      if ( myProp2 ) {
        onPropChange( Js.uncheckedCast( props.getAsAny( Props.myProp2 ) ) );
      }
      if ( myProp3 ) {
        onPropChange( props.getAsAny( Props.myProp3 ).asInt() );
      }
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_CustomNamingOnPropChange) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp1 = React.shouldMinimizePropKeys() ? "a" : "myProp1";

    static final String myProp2 = React.shouldMinimizePropKeys() ? "b" : "myProp2";

    static final String myProp3 = React.shouldMinimizePropKeys() ? "c" : "myProp3";
  }

  private static final class LiteNativeReactComponent extends NativeComponent {
    private React4j_CustomNamingOnPropChange $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_CustomNamingOnPropChange( this );
    }

    @JsMethod
    protected final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @JsMethod
    protected final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_component.$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @JsMethod
    @Nullable
    protected final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent {
    private React4j_CustomNamingOnPropChange $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_CustomNamingOnPropChange( this );
    }

    @JsMethod
    protected final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @JsMethod
    protected final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_component.$$react4j$$_componentPreUpdate( prevProps );
      return null;
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
