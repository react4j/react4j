package com.example.prop;

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
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "MultiPropComponent4",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MultiPropComponent4 extends MultiPropComponent4 {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MultiPropComponent4(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "MultiPropComponent4" );
    }
    return viewConstructor;
  }

  @Override
  String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.myProp ) ? $$react4j$$_nativeView.props().getAsAny( Props.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.myProp ) );
    }
  }

  @Nullable
  @Override
  String getMyProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.myProp2 ) ? $$react4j$$_nativeView.props().getAsAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.myProp2 ) );
    }
  }

  @Nullable
  @Override
  String getMyProp3() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.myProp3 ) ? $$react4j$$_nativeView.props().getAsAny( Props.myProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.myProp3 ) );
    }
  }

  @Nullable
  @Override
  String getMyProp4() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.myProp4 ) ? $$react4j$$_nativeView.props().getAsAny( Props.myProp4 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.myProp4 ) );
    }
  }

  @Nullable
  @Override
  ReactNode getChild() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.child ) ? $$react4j$$_nativeView.props().getAsAny( Props.child ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.child ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = $$react4j$$_nativeView.props();
    if ( !Js.isTripleEqual( props.get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myProp2 ), nextProps.get( Props.myProp2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myProp3 ), nextProps.get( Props.myProp3 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.myProp4 ), nextProps.get( Props.myProp4 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.child ), nextProps.get( Props.child ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MultiPropComponent4) this).dispose();
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
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";

    static final String myProp2 = React.shouldMinimizePropKeys() ? "b" : "myProp2";

    static final String myProp3 = React.shouldMinimizePropKeys() ? "c" : "myProp3";

    static final String myProp4 = React.shouldMinimizePropKeys() ? "d" : "myProp4";

    static final String child = "children";
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_MultiPropComponent4 $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_MultiPropComponent4( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_MultiPropComponent4 $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_MultiPropComponent4( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextProps );
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
