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
import react4j.internal.OnShouldComponentUpdate;

@ArezComponent(
    name = "MultiPropComponent2",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_MultiPropComponent2 extends MultiPropComponent2 {
  React4j_MultiPropComponent2(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent2" );
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

  @Override
  protected String getMyProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp2 ) ? props().getAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp2 ) );
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

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myProp2 ), nextProps.get( Props.myProp2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.children ), nextProps.get( Props.children ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MultiPropComponent2) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";

    static final String myProp2 = React.shouldMinimizePropKeys() ? "b" : "myProp2";

    static final String children = "children";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate {
    private React4j_MultiPropComponent2 $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_MultiPropComponent2( this );
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
    private React4j_MultiPropComponent2 $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_MultiPropComponent2( this );
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
