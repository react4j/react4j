package com.example.optional_props;

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
    name = "com_example_optional_props_RequiredChildrenWithManyRequired",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_RequiredChildrenWithManyRequired extends RequiredChildrenWithManyRequired {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_RequiredChildrenWithManyRequired(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "RequiredChildrenWithManyRequired" );
    }
    return viewConstructor;
  }

  @Override
  String getMyRequiredProp1() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp1 ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp1 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp1 ) );
    }
  }

  @Override
  String getMyRequiredProp2() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp2 ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp2 ) );
    }
  }

  @Override
  String getMyRequiredProp3() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp3 ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp3 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp3 ) );
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
    if ( !Js.isTripleEqual( inputs.get( Inputs.myRequiredProp1 ), nextInputs.get( Inputs.myRequiredProp1 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myRequiredProp2 ), nextInputs.get( Inputs.myRequiredProp2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myRequiredProp3 ), nextInputs.get( Inputs.myRequiredProp3 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.children ), nextInputs.get( Inputs.children ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_RequiredChildrenWithManyRequired) this).dispose();
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
    static final String myRequiredProp1 = React.shouldMinimizeInputKeys() ? "a" : "myRequiredProp1";

    static final String myRequiredProp2 = React.shouldMinimizeInputKeys() ? "b" : "myRequiredProp2";

    static final String myRequiredProp3 = React.shouldMinimizeInputKeys() ? "c" : "myRequiredProp3";

    static final String children = "children";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_RequiredChildrenWithManyRequired $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_RequiredChildrenWithManyRequired( this );
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
