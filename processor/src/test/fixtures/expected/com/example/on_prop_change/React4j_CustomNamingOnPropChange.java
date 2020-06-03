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
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "CustomNamingOnPropChange",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_CustomNamingOnPropChange extends CustomNamingOnPropChange {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_CustomNamingOnPropChange(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "CustomNamingOnPropChange" );
    }
    return viewConstructor;
  }

  @Override
  boolean getMyProp1() {
    return $$react4j$$_nativeView.props().getAsAny( Props.myProp1 ).asBoolean();
  }

  @Override
  String getMyProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.myProp2 ) ? $$react4j$$_nativeView.props().getAsAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.myProp2 ) );
    }
  }

  @Override
  int getMyProp3() {
    return $$react4j$$_nativeView.props().getAsAny( Props.myProp3 ).asInt();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = $$react4j$$_nativeView.props();
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
      final JsPropertyMap<Object> props = $$react4j$$_nativeView.props();
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

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp1 = React.shouldMinimizePropKeys() ? "a" : "myProp1";

    static final String myProp2 = React.shouldMinimizePropKeys() ? "b" : "myProp2";

    static final String myProp3 = React.shouldMinimizePropKeys() ? "c" : "myProp3";
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnGetSnapshotBeforeUpdate {
    @Nonnull
    private final React4j_CustomNamingOnPropChange $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_CustomNamingOnPropChange( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_view.$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount, OnGetSnapshotBeforeUpdate {
    @Nonnull
    private final React4j_CustomNamingOnPropChange $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_CustomNamingOnPropChange( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_view.$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_view.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }
}
