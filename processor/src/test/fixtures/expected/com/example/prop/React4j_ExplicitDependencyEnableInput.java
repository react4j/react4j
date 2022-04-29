package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "com_example_prop_ExplicitDependencyEnableInput",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ExplicitDependencyEnableInput extends ExplicitDependencyEnableInput {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  @ComponentDependency
  final ExplicitDependencyEnableInput.MyComponent $$react4j_immutable_input$$_myComponent;

  React4j_ExplicitDependencyEnableInput(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_myComponent = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myComponent ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myComponent ).cast() : null;
    } else {
      $$react4j_immutable_input$$_myComponent = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myComponent ) );
    }
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ExplicitDependencyEnableInput" );
    }
    return viewConstructor;
  }

  @Override
  ExplicitDependencyEnableInput.MyComponent getMyComponent() {
    return $$react4j_immutable_input$$_myComponent;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ExplicitDependencyEnableInput) this).dispose();
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

  static final class Inputs {
    static final String myComponent = React.shouldMinimizeInputKeys() ? "a" : "myComponent";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ExplicitDependencyEnableInput $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_ExplicitDependencyEnableInput( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      if ( Disposable.isNotDisposed( $$react4j$$_view ) ) {
        return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
      } else {
        return false;
      }
    }

    @Override
    public final void componentWillUnmount() {
      if ( Disposable.isNotDisposed( $$react4j$$_view ) ) {
        $$react4j$$_view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    @Nullable
    public final ReactNode render() {
      if ( Disposable.isNotDisposed( $$react4j$$_view ) ) {
        return $$react4j$$_view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }
}
