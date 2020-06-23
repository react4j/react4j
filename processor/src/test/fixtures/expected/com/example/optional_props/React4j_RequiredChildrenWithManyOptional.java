package com.example.optional_props;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
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

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
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
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_RequiredChildrenWithManyOptional(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidateInputValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "RequiredChildrenWithManyOptional" );
    }
    return viewConstructor;
  }

  @Override
  String getMyPropA() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropA ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropA ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropA ) );
    }
  }

  @Override
  String getMyPropB() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropB ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropB ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropB ) );
    }
  }

  @Override
  String getMyPropC() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropC ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropC ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropC ) );
    }
  }

  @Override
  String getMyPropD() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropD ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropD ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myPropD ) );
    }
  }

  @Override
  ReactNode[] getChildren() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.children ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.children ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.children ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
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
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String myPropA = React.shouldMinimizeInputKeys() ? "a" : "myPropA";

    static final String myPropB = React.shouldMinimizeInputKeys() ? "b" : "myPropB";

    static final String myPropC = React.shouldMinimizeInputKeys() ? "c" : "myPropC";

    static final String myPropD = React.shouldMinimizeInputKeys() ? "d" : "myPropD";

    static final String children = "children";
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_RequiredChildrenWithManyOptional $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_RequiredChildrenWithManyOptional( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_RequiredChildrenWithManyOptional $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_RequiredChildrenWithManyOptional( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
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
