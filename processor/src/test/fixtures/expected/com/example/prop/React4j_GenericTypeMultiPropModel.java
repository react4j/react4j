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
    name = "GenericTypeMultiPropModel",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_GenericTypeMultiPropModel<T> extends GenericTypeMultiPropModel<T> {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_GenericTypeMultiPropModel(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "GenericTypeMultiPropModel" );
    }
    return viewConstructor;
  }

  @Override
  T getValue() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.value ) ? $$react4j$$_nativeView.props().getAsAny( Props.value ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.value ) );
    }
  }

  @Override
  String getValue2() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.value2 ) ? $$react4j$$_nativeView.props().getAsAny( Props.value2 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.value2 ) );
    }
  }

  @Nullable
  @Override
  String getValue3() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.value3 ) ? $$react4j$$_nativeView.props().getAsAny( Props.value3 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.value3 ) );
    }
  }

  @Nullable
  @Override
  String getValue4() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.props().getAsAny( Props.value4 ) ? $$react4j$$_nativeView.props().getAsAny( Props.value4 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.props().getAsAny( Props.value4 ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = $$react4j$$_nativeView.props();
    if ( !Js.isTripleEqual( props.get( Props.value ), nextProps.get( Props.value ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.value2 ), nextProps.get( Props.value2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.value3 ), nextProps.get( Props.value3 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( props.get( Props.value4 ), nextProps.get( Props.value4 ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_GenericTypeMultiPropModel) this).dispose();
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
    static final String value = React.shouldMinimizePropKeys() ? "a" : "value";

    static final String value2 = React.shouldMinimizePropKeys() ? "b" : "value2";

    static final String value3 = React.shouldMinimizePropKeys() ? "c" : "value3";

    static final String value4 = React.shouldMinimizePropKeys() ? "d" : "value4";
  }

  private static final class LiteNativeView<T> extends react4j.internal.NativeView implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_GenericTypeMultiPropModel<T> $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_GenericTypeMultiPropModel<T>( this );
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

  private static final class NativeView<T> extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_GenericTypeMultiPropModel<T> $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_GenericTypeMultiPropModel<T>( this );
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
