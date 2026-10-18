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
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "com_example_prop_ImmutablePropTypes",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypes extends ImmutablePropTypes {
  React4j_ImmutablePropTypes(final react4j.internal.NativeView $$react4j$$_nativeView,
      final ImmutablePropTypes. @Nullable MyComponent myProp, final @Nullable String myOtherProp,
      final int stillAnotherProp, final ImmutablePropTypes. @Nullable KeyedComponent bobsProp,
      final ImmutablePropTypes. @Nullable Foo someProp, final @Nullable Object object) {
    super( myProp, myOtherProp, stillAnotherProp, bobsProp, someProp, object );
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast( viewConstructor ).set( "displayName", "ImmutablePropTypes" );
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$stillAnotherProp = inputs.get( Inputs.stillAnotherProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$stillAnotherProp, () -> "Required input named 'stillAnotherProp' is missing from view named 'ImmutablePropTypes' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypes) this).dispose();
  }

  @Nullable ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
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
    private final @Nullable React4j_ImmutablePropTypes view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      assert null != inputs;
      ImmutablePropTypes.MyComponent myProp;
      if ( React.shouldCheckInvariants() ) {
        final var raw$myProp = inputs.getAsAny( Inputs.myProp );
        myProp = null != raw$myProp ? raw$myProp.cast() : null;
      } else {
        myProp = Js.uncheckedCast( inputs.getAsAny( Inputs.myProp ) );
      }
      String myOtherProp;
      if ( React.shouldCheckInvariants() ) {
        final var raw$myOtherProp = inputs.getAsAny( Inputs.myOtherProp );
        myOtherProp = null != raw$myOtherProp ? raw$myOtherProp.asString() : null;
      } else {
        myOtherProp = Js.uncheckedCast( inputs.getAsAny( Inputs.myOtherProp ) );
      }
      final int stillAnotherProp = Objects.requireNonNull( inputs.getAsAny( Inputs.stillAnotherProp ) ).asInt();
      ImmutablePropTypes.KeyedComponent bobsProp;
      if ( React.shouldCheckInvariants() ) {
        final var raw$bobsProp = inputs.getAsAny( Inputs.bobsProp );
        bobsProp = null != raw$bobsProp ? raw$bobsProp.cast() : null;
      } else {
        bobsProp = Js.uncheckedCast( inputs.getAsAny( Inputs.bobsProp ) );
      }
      ImmutablePropTypes.Foo someProp;
      if ( React.shouldCheckInvariants() ) {
        final var raw$someProp = inputs.getAsAny( Inputs.someProp );
        someProp = null != raw$someProp ? raw$someProp.cast() : null;
      } else {
        someProp = Js.uncheckedCast( inputs.getAsAny( Inputs.someProp ) );
      }
      Object object;
      if ( React.shouldCheckInvariants() ) {
        final var raw$object = inputs.getAsAny( Inputs.object );
        object = null != raw$object ? raw$object.cast() : null;
      } else {
        object = Js.uncheckedCast( inputs.getAsAny( Inputs.object ) );
      }
      view = Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.myProp ) ) ) ? null : new Arez_React4j_ImmutablePropTypes( this, myProp, myOtherProp, stillAnotherProp, bobsProp, someProp, object );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_ImmutablePropTypes.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_shouldComponentUpdate( nextInputs );
      } else {
        return false;
      }
    }

    @Override
    public final void componentWillUnmount() {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    public final @Nullable ReactNode render() {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }
}
