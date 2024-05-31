package com.example.inheritance.complex_interface;

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
    name = "com_example_inheritance_complex_interface_MyView",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MyView extends MyView {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MyView(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "MyView" );
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
  public boolean zzField1InInnerInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.zzField1InInnerInterface ).asBoolean();
  }

  @Override
  public boolean aaField2InInnerInterface() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.aaField2InInnerInterface ).asBoolean();
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
    if ( !Js.isTripleEqual( inputs.get( Inputs.zzField1InInnerInterface ), nextInputs.get( Inputs.zzField1InInnerInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.aaField2InInnerInterface ), nextInputs.get( Inputs.aaField2InInnerInterface ) ) ) {
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
    ((Arez_React4j_MyView) this).dispose();
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

    static final String zzField1InInnerInterface = React.shouldMinimizeInputKeys() ? "c" : "zzField1InInnerInterface";

    static final String aaField2InInnerInterface = React.shouldMinimizeInputKeys() ? "d" : "aaField2InInnerInterface";

    static final String zzField1InLeafInterface = React.shouldMinimizeInputKeys() ? "e" : "zzField1InLeafInterface";

    static final String aaField2InLeafInterface = React.shouldMinimizeInputKeys() ? "f" : "aaField2InLeafInterface";

    static final String field1InLeaf = React.shouldMinimizeInputKeys() ? "g" : "field1InLeaf";

    static final String field2InLeaf = React.shouldMinimizeInputKeys() ? "h" : "field2InLeaf";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_MyView $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_MyView( this );
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
