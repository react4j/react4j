package com.example.on_prop_change;

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
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "MultipleOnPropChange",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MultipleOnPropChange extends MultipleOnPropChange {
  @Nonnull
  private final NativeComponent $$react4j$$_nativeComponent;

  React4j_MultipleOnPropChange(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    this.$$react4j$$_nativeComponent = Objects.requireNonNull( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultipleOnPropChange" );
    }
    return componentConstructor;
  }

  @Override
  boolean getMyProp1() {
    return $$react4j$$_nativeComponent.props().getAsAny( Props.myProp1 ).asBoolean();
  }

  @Override
  String getMyProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myProp2 ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myProp2 ) );
    }
  }

  @Override
  int getMyProp3() {
    return $$react4j$$_nativeComponent.props().getAsAny( Props.myProp3 ).asInt();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = $$react4j$$_nativeComponent.props();
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
      final JsPropertyMap<Object> props = $$react4j$$_nativeComponent.props();
      final boolean myProp1 = !Js.isTripleEqual( props.get( Props.myProp1 ), prevProps.get( Props.myProp1 ) );
      final boolean myProp2 = !Js.isTripleEqual( props.get( Props.myProp2 ), prevProps.get( Props.myProp2 ) );
      final boolean myProp3 = !Js.isTripleEqual( props.get( Props.myProp3 ), prevProps.get( Props.myProp3 ) );
      if ( myProp1 && myProp2 && myProp3 ) {
        onPropChange( props.getAsAny( Props.myProp1 ).asBoolean(), Js.uncheckedCast( props.getAsAny( Props.myProp2 ) ), props.getAsAny( Props.myProp3 ).asInt() );
      }
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MultipleOnPropChange) this).dispose();
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
    static final String myProp1 = React.shouldMinimizePropKeys() ? "a" : "myProp1";

    static final String myProp2 = React.shouldMinimizePropKeys() ? "b" : "myProp2";

    static final String myProp3 = React.shouldMinimizePropKeys() ? "c" : "myProp3";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate, OnGetSnapshotBeforeUpdate {
    @Nonnull
    private final React4j_MultipleOnPropChange $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_MultipleOnPropChange( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_component.$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.$$react4j$$_render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnShouldComponentUpdate, OnComponentWillUnmount, OnGetSnapshotBeforeUpdate {
    @Nonnull
    private final React4j_MultipleOnPropChange $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_MultipleOnPropChange( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_component.$$react4j$$_componentPreUpdate( prevProps );
      return null;
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
