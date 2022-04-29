package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
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
    name = "com_example_prop_ImmutablePropTypes",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypes extends ImmutablePropTypes {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  @ComponentDependency
  final ImmutablePropTypes.MyComponent $$react4j_immutable_input$$_myProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final String $$react4j_immutable_input$$_myOtherProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final int $$react4j_immutable_input$$_stillAnotherProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final ImmutablePropTypes.KeyedComponent $$react4j_immutable_input$$_BobsProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final ImmutablePropTypes.Foo $$react4j_immutable_input$$_someProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final Object $$react4j_immutable_input$$_object;

  React4j_ImmutablePropTypes(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_myProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_myProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_myOtherProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOtherProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOtherProp ).asString() : null;
    } else {
      $$react4j_immutable_input$$_myOtherProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOtherProp ) );
    }
    $$react4j_immutable_input$$_stillAnotherProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.stillAnotherProp ).asInt();
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_BobsProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.BobsProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.BobsProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_BobsProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.BobsProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_someProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.someProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.someProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_someProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.someProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_object = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.object ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.object ).cast() : null;
    } else {
      $$react4j_immutable_input$$_object = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.object ) );
    }
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutablePropTypes" );
    }
    return viewConstructor;
  }

  @Override
  ImmutablePropTypes.MyComponent getMyProp() {
    return $$react4j_immutable_input$$_myProp;
  }

  @Override
  String getMyOtherProp() {
    return $$react4j_immutable_input$$_myOtherProp;
  }

  @Override
  int stillAnotherProp() {
    return $$react4j_immutable_input$$_stillAnotherProp;
  }

  @Override
  ImmutablePropTypes.KeyedComponent BobsProp() {
    return $$react4j_immutable_input$$_BobsProp;
  }

  @Override
  ImmutablePropTypes.Foo getSomeProp() {
    return $$react4j_immutable_input$$_someProp;
  }

  @Override
  Object getObject() {
    return $$react4j_immutable_input$$_object;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    return false;
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
  }

  static final class Inputs {
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";

    static final String myOtherProp = React.shouldMinimizeInputKeys() ? "b" : "myOtherProp";

    static final String stillAnotherProp = React.shouldMinimizeInputKeys() ? "c" : "stillAnotherProp";

    static final String BobsProp = React.shouldMinimizeInputKeys() ? "d" : "BobsProp";

    static final String someProp = React.shouldMinimizeInputKeys() ? "e" : "someProp";

    static final String object = React.shouldMinimizeInputKeys() ? "f" : "object";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutablePropTypes $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_ImmutablePropTypes( this );
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
