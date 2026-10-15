package com.example.optional_props;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "com_example_optional_props_RequiredChildrenWithManyOptional",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_RequiredChildrenWithManyOptional extends RequiredChildrenWithManyOptional {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_RequiredChildrenWithManyOptional(
      final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "RequiredChildrenWithManyOptional" );
    }
    return viewConstructor;
  }

  @Override
  @Nullable String getMyPropA() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropA ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropA ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropA ) );
    }
  }

  @Override
  @Nullable String getMyPropB() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropB ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropB ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropB ) );
    }
  }

  @Override
  @Nullable String getMyPropC() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropC ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropC ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropC ) );
    }
  }

  @Override
  @Nullable String getMyPropD() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropD ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropD ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myPropD ) );
    }
  }

  @Override
  ReactNode @Nullable [] getChildren() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.children ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.children ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.children ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myPropA ), nextInputs.get( Inputs.myPropA ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myPropB ), nextInputs.get( Inputs.myPropB ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myPropC ), nextInputs.get( Inputs.myPropC ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myPropD ), nextInputs.get( Inputs.myPropD ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.children ), nextInputs.get( Inputs.children ) ) ) {
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
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String myPropA = React.shouldMinimizeInputKeys() ? "a" : "myPropA";

    static final String myPropB = React.shouldMinimizeInputKeys() ? "b" : "myPropB";

    static final String myPropC = React.shouldMinimizeInputKeys() ? "c" : "myPropC";

    static final String myPropD = React.shouldMinimizeInputKeys() ? "d" : "myPropD";

    static final String children = "children";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @NonNull
    private final React4j_RequiredChildrenWithManyOptional view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_RequiredChildrenWithManyOptional( this );
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
    @Nullable
    public final ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
