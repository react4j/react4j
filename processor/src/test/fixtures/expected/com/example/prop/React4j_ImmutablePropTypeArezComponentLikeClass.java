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
    name = "com_example_prop_ImmutablePropTypeArezComponentLikeClass",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypeArezComponentLikeClass extends ImmutablePropTypeArezComponentLikeClass {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  @ComponentDependency
  @Nullable
  final ImmutablePropTypeArezComponentLikeClass.MyComponent $$react4j_immutable_input$$_myProp;

  React4j_ImmutablePropTypeArezComponentLikeClass(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView,
      @Nullable final ImmutablePropTypeArezComponentLikeClass.MyComponent myProp) {
    super( myProp );
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
    $$react4j_immutable_input$$_myProp = myProp;
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutablePropTypeArezComponentLikeClass" );
    }
    return viewConstructor;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypeArezComponentLikeClass) this).dispose();
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
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutablePropTypeArezComponentLikeClass view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      ImmutablePropTypeArezComponentLikeClass.MyComponent myProp;
      if ( React.shouldCheckInvariants() ) {
        myProp = null != inputs.getAsAny( Inputs.myProp ) ? inputs.getAsAny( Inputs.myProp ).cast() : null;
      } else {
        myProp = Js.uncheckedCast( inputs.getAsAny( Inputs.myProp ) );
      }
      view = Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.myProp ) ) ) ? null : new Arez_React4j_ImmutablePropTypeArezComponentLikeClass( this, myProp );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      if ( Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_shouldComponentUpdate( nextInputs );
      } else {
        return false;
      }
    }

    @Override
    public final void componentWillUnmount() {
      if ( Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    @Nullable
    public final ReactNode render() {
      if ( Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }
}
