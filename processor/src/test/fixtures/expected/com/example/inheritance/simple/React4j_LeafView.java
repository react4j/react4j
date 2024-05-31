package com.example.inheritance.simple;

import arez.Disposable;
import arez.annotations.ArezComponent;
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
    name = "com_example_inheritance_simple_LeafView",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_LeafView extends LeafView {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_LeafView(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "LeafView" );
    }
    return viewConstructor;
  }

  @Override
  public boolean zzField1InRootInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.zzField1InRootInterface ).asBoolean();
  }

  @Override
  public boolean aaField2InRootInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.aaField2InRootInterface ).asBoolean();
  }

  @Override
  boolean field1InRoot() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.field1InRoot ).asBoolean();
  }

  @Override
  boolean field2InRoot() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.field2InRoot ).asBoolean();
  }

  @Override
  public boolean zzField1InInnerInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.zzField1InInnerInterface ).asBoolean();
  }

  @Override
  public boolean aaField2InInnerInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.aaField2InInnerInterface ).asBoolean();
  }

  @Override
  boolean field1InInner() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.field1InInner ).asBoolean();
  }

  @Override
  boolean field2InInner() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.field2InInner ).asBoolean();
  }

  @Override
  public boolean zzField1InLeafInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.zzField1InLeafInterface ).asBoolean();
  }

  @Override
  public boolean aaField2InLeafInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.aaField2InLeafInterface ).asBoolean();
  }

  @Override
  boolean field1InLeaf() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.field1InLeaf ).asBoolean();
  }

  @Override
  boolean field2InLeaf() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.field2InLeaf ).asBoolean();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.zzField1InRootInterface ), nextInputs.get( Inputs.zzField1InRootInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.aaField2InRootInterface ), nextInputs.get( Inputs.aaField2InRootInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field1InRoot ), nextInputs.get( Inputs.field1InRoot ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field2InRoot ), nextInputs.get( Inputs.field2InRoot ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.zzField1InInnerInterface ), nextInputs.get( Inputs.zzField1InInnerInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.aaField2InInnerInterface ), nextInputs.get( Inputs.aaField2InInnerInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field1InInner ), nextInputs.get( Inputs.field1InInner ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field2InInner ), nextInputs.get( Inputs.field2InInner ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.zzField1InLeafInterface ), nextInputs.get( Inputs.zzField1InLeafInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.aaField2InLeafInterface ), nextInputs.get( Inputs.aaField2InLeafInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field1InLeaf ), nextInputs.get( Inputs.field1InLeaf ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field2InLeaf ), nextInputs.get( Inputs.field2InLeaf ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_LeafView) this).dispose();
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
    static final String zzField1InRootInterface = React.shouldMinimizeInputKeys() ? "a" : "zzField1InRootInterface";

    static final String aaField2InRootInterface = React.shouldMinimizeInputKeys() ? "b" : "aaField2InRootInterface";

    static final String field1InRoot = React.shouldMinimizeInputKeys() ? "c" : "field1InRoot";

    static final String field2InRoot = React.shouldMinimizeInputKeys() ? "d" : "field2InRoot";

    static final String zzField1InInnerInterface = React.shouldMinimizeInputKeys() ? "e" : "zzField1InInnerInterface";

    static final String aaField2InInnerInterface = React.shouldMinimizeInputKeys() ? "f" : "aaField2InInnerInterface";

    static final String field1InInner = React.shouldMinimizeInputKeys() ? "g" : "field1InInner";

    static final String field2InInner = React.shouldMinimizeInputKeys() ? "h" : "field2InInner";

    static final String zzField1InLeafInterface = React.shouldMinimizeInputKeys() ? "i" : "zzField1InLeafInterface";

    static final String aaField2InLeafInterface = React.shouldMinimizeInputKeys() ? "j" : "aaField2InLeafInterface";

    static final String field1InLeaf = React.shouldMinimizeInputKeys() ? "k" : "field1InLeaf";

    static final String field2InLeaf = React.shouldMinimizeInputKeys() ? "l" : "field2InLeaf";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_LeafView $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_LeafView( this );
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
