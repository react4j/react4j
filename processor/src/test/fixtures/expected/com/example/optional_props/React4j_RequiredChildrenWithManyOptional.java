package com.example.optional_props;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import java.util.Objects;
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
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_RequiredChildrenWithManyOptional extends RequiredChildrenWithManyOptional {
  @Nonnull
  private final NativeComponent $$react4j$$_nativeComponent;

  React4j_RequiredChildrenWithManyOptional(
      @Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    this.$$react4j$$_nativeComponent = Objects.requireNonNull( $$react4j$$_nativeComponent );
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
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myPropA ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myPropA ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myPropA ) );
    }
  }

  @Override
  String getMyPropB() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myPropB ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myPropB ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myPropB ) );
    }
  }

  @Override
  String getMyPropC() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myPropC ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myPropC ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myPropC ) );
    }
  }

  @Override
  String getMyPropD() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myPropD ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myPropD ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myPropD ) );
    }
  }

  @Override
  ReactNode[] getChildren() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.children ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.children ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.children ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = $$react4j$$_nativeComponent.props();
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

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
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
      return $$react4j$$_component.$$react4j$$_render();
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
      return $$react4j$$_component.$$react4j$$_render();
    }
  }
}
