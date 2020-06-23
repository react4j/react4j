package com.example.on_prop_change;

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
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
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
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MultipleOnPropChange(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidateInputValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "MultipleOnPropChange" );
    }
    return viewConstructor;
  }

  @Override
  boolean getMyProp1() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp1 ).asBoolean();
  }

  @Override
  String getMyProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp2 ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp2 ) );
    }
  }

  @Override
  int getMyProp3() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp3 ).asInt();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myProp1 ), nextInputs.get( Inputs.myProp1 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myProp2 ), nextInputs.get( Inputs.myProp2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myProp3 ), nextInputs.get( Inputs.myProp3 ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevInputs) {
    if ( null != prevInputs ) {
      final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
      final boolean myProp1 = !Js.isTripleEqual( inputs.get( Inputs.myProp1 ), prevInputs.get( Inputs.myProp1 ) );
      final boolean myProp2 = !Js.isTripleEqual( inputs.get( Inputs.myProp2 ), prevInputs.get( Inputs.myProp2 ) );
      final boolean myProp3 = !Js.isTripleEqual( inputs.get( Inputs.myProp3 ), prevInputs.get( Inputs.myProp3 ) );
      if ( myProp1 && myProp2 && myProp3 ) {
        onPropChange( inputs.getAsAny( Inputs.myProp1 ).asBoolean(), Js.uncheckedCast( inputs.getAsAny( Inputs.myProp2 ) ), inputs.getAsAny( Inputs.myProp3 ).asInt() );
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
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String myProp1 = React.shouldMinimizeInputKeys() ? "a" : "myProp1";

    static final String myProp2 = React.shouldMinimizeInputKeys() ? "b" : "myProp2";

    static final String myProp3 = React.shouldMinimizeInputKeys() ? "c" : "myProp3";
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnGetSnapshotBeforeUpdate {
    @Nonnull
    private final React4j_MultipleOnPropChange $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_MultipleOnPropChange( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevInputs,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_view.$$react4j$$_componentPreUpdate( prevInputs );
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
    private final React4j_MultipleOnPropChange $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_MultipleOnPropChange( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevInputs,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_view.$$react4j$$_componentPreUpdate( prevInputs );
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
