package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "com_example_prop_MultiContextPropModel",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MultiContextPropModel extends MultiContextPropModel {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MultiContextPropModel(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast( viewConstructor ).set( "displayName", "MultiContextPropModel" );
    }
    return viewConstructor;
  }

  @Override
  @Nullable String getStringContextValue() {
    if ( React.shouldCheckInvariants() ) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.stringContextValue );
      return null != value ? value.asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.stringContextValue ) );
    }
  }

  @Override
  int getIntContextValue() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.intContextValue ) ).asInt();
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$intContextValue = inputs.get( Inputs.intContextValue );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$intContextValue, () -> "Context value of type int is missing when constructing view named 'MultiContextPropModel'. Ensure a parent view publishes the value to the context." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.stringContextValue ), nextInputs.get( Inputs.stringContextValue ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.intContextValue ), nextInputs.get( Inputs.intContextValue ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MultiContextPropModel) this).dispose();
  }

  @Nullable ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return Renderable.toReactNode( render() );
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String stringContextValue = React.shouldMinimizeInputKeys() ? "a" : "stringContextValue";

    static final String intContextValue = React.shouldMinimizeInputKeys() ? "b" : "intContextValue";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_MultiContextPropModel view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_MultiContextPropModel( this );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_MultiContextPropModel.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final void componentWillUnmount() {
      view.$$react4j$$_componentWillUnmount();
    }

    @Override
    public final @Nullable ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
