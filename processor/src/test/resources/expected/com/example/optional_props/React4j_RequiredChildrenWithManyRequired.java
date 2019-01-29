package com.example.optional_props;

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
    name = "RequiredChildrenWithManyRequired",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true,
    inject = InjectMode.NONE,
    dagger = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_RequiredChildrenWithManyRequired extends RequiredChildrenWithManyRequired {
  React4j_RequiredChildrenWithManyRequired(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyRequired" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyRequiredProp1() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myRequiredProp1 ) ? props().getAny( Props.myRequiredProp1 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp1 ) );
    }
  }

  @Override
  protected String getMyRequiredProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myRequiredProp2 ) ? props().getAny( Props.myRequiredProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp2 ) );
    }
  }

  @Override
  protected String getMyRequiredProp3() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myRequiredProp3 ) ? props().getAny( Props.myRequiredProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myRequiredProp3 ) );
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
    if ( !Js.isTripleEqual( props.get( Props.myRequiredProp1 ), nextProps.get( Props.myRequiredProp1 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myRequiredProp2 ), nextProps.get( Props.myRequiredProp2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myRequiredProp3 ), nextProps.get( Props.myRequiredProp3 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.children ), nextProps.get( Props.children ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_RequiredChildrenWithManyRequired) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myRequiredProp1 = React.shouldMinimizePropKeys() ? "a" : "myRequiredProp1";

    static final String myRequiredProp2 = React.shouldMinimizePropKeys() ? "b" : "myRequiredProp2";

    static final String myRequiredProp3 = React.shouldMinimizePropKeys() ? "c" : "myRequiredProp3";

    static final String children = "children";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate {
    private React4j_RequiredChildrenWithManyRequired $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_RequiredChildrenWithManyRequired( this );
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
    private React4j_RequiredChildrenWithManyRequired $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_RequiredChildrenWithManyRequired( this );
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
