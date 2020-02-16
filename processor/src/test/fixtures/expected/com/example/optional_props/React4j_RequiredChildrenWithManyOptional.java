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
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "RequiredChildrenWithManyOptional",
    disposeNotifier = Feature.DISABLE,
    allowEmpty = true,
    dagger = Feature.DISABLE
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_RequiredChildrenWithManyOptional extends RequiredChildrenWithManyOptional {
  React4j_RequiredChildrenWithManyOptional(
      @Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyOptional" );
    }
    return componentConstructor;
  }

  @Override
  String getMyPropA() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.myPropA ) ? props().getAsAny( Props.myPropA ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.myPropA ) );
    }
  }

  @Override
  String getMyPropB() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.myPropB ) ? props().getAsAny( Props.myPropB ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.myPropB ) );
    }
  }

  @Override
  String getMyPropC() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.myPropC ) ? props().getAsAny( Props.myPropC ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.myPropC ) );
    }
  }

  @Override
  String getMyPropD() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.myPropD ) ? props().getAsAny( Props.myPropD ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.myPropD ) );
    }
  }

  @Override
  ReactNode[] getChildren() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.children ) ? props().getAsAny( Props.children ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.children ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.myPropA ), nextProps.get( Props.myPropA ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myPropB ), nextProps.get( Props.myPropB ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myPropC ), nextProps.get( Props.myPropC ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myPropD ), nextProps.get( Props.myPropD ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.children ), nextProps.get( Props.children ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_RequiredChildrenWithManyOptional) this).dispose();
  }

  static final class Factory {
    @Nonnull
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myPropA = React.shouldMinimizePropKeys() ? "a" : "myPropA";

    static final String myPropB = React.shouldMinimizePropKeys() ? "b" : "myPropB";

    static final String myPropC = React.shouldMinimizePropKeys() ? "c" : "myPropC";

    static final String myPropD = React.shouldMinimizePropKeys() ? "d" : "myPropD";

    static final String children = "children";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_RequiredChildrenWithManyOptional $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_RequiredChildrenWithManyOptional( this );
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
    private final React4j_RequiredChildrenWithManyOptional $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_RequiredChildrenWithManyOptional( this );
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
