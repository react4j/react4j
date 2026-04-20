package com.example.prop;

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
    name = "com_example_prop_ImmutablePropTypes",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypes extends ImmutablePropTypes {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_ImmutablePropTypes(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView,
      @Nullable final ImmutablePropTypes.MyComponent myProp, @Nullable final String myOtherProp,
      final int stillAnotherProp, @Nullable final ImmutablePropTypes.KeyedComponent bobsProp,
      @Nullable final ImmutablePropTypes.Foo someProp, @Nullable final Object object) {
    super( myProp, myOtherProp, stillAnotherProp, bobsProp, someProp, object );
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutablePropTypes" );
    }
    return viewConstructor;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypes) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String stillAnotherProp = React.shouldMinimizeInputKeys() ? "a" : "stillAnotherProp";

    static final String myProp = React.shouldMinimizeInputKeys() ? "b" : "myProp";

    static final String myOtherProp = React.shouldMinimizeInputKeys() ? "c" : "myOtherProp";

    static final String bobsProp = React.shouldMinimizeInputKeys() ? "d" : "bobsProp";

    static final String someProp = React.shouldMinimizeInputKeys() ? "e" : "someProp";

    static final String object = React.shouldMinimizeInputKeys() ? "f" : "object";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutablePropTypes view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      ImmutablePropTypes.MyComponent myProp;
      if ( React.shouldCheckInvariants() ) {
        myProp = null != inputs.getAsAny( Inputs.myProp ) ? inputs.getAsAny( Inputs.myProp ).cast() : null;
      } else {
        myProp = Js.uncheckedCast( inputs.getAsAny( Inputs.myProp ) );
      }
      String myOtherProp;
      if ( React.shouldCheckInvariants() ) {
        myOtherProp = null != inputs.getAsAny( Inputs.myOtherProp ) ? inputs.getAsAny( Inputs.myOtherProp ).asString() : null;
      } else {
        myOtherProp = Js.uncheckedCast( inputs.getAsAny( Inputs.myOtherProp ) );
      }
      final int stillAnotherProp = inputs.getAsAny( Inputs.stillAnotherProp ).asInt();
      ImmutablePropTypes.KeyedComponent bobsProp;
      if ( React.shouldCheckInvariants() ) {
        bobsProp = null != inputs.getAsAny( Inputs.bobsProp ) ? inputs.getAsAny( Inputs.bobsProp ).cast() : null;
      } else {
        bobsProp = Js.uncheckedCast( inputs.getAsAny( Inputs.bobsProp ) );
      }
      ImmutablePropTypes.Foo someProp;
      if ( React.shouldCheckInvariants() ) {
        someProp = null != inputs.getAsAny( Inputs.someProp ) ? inputs.getAsAny( Inputs.someProp ).cast() : null;
      } else {
        someProp = Js.uncheckedCast( inputs.getAsAny( Inputs.someProp ) );
      }
      Object object;
      if ( React.shouldCheckInvariants() ) {
        object = null != inputs.getAsAny( Inputs.object ) ? inputs.getAsAny( Inputs.object ).cast() : null;
      } else {
        object = Js.uncheckedCast( inputs.getAsAny( Inputs.object ) );
      }
      view = Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.myProp ) ) ) ? null : new Arez_React4j_ImmutablePropTypes( this, myProp, myOtherProp, stillAnotherProp, bobsProp, someProp, object );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return false;
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
