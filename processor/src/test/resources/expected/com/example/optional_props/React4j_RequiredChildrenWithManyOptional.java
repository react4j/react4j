package com.example.optional_props;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentWillUnmount;

@ArezComponent(
    name = "RequiredChildrenWithManyOptional",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_RequiredChildrenWithManyOptional extends RequiredChildrenWithManyOptional {
  React4j_RequiredChildrenWithManyOptional(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RequiredChildrenWithManyOptional" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyPropA() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropA ) ? props().getAny( Props.myPropA ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropA ) );
    }
  }

  @Override
  protected String getMyPropB() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropB ) ? props().getAny( Props.myPropB ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropB ) );
    }
  }

  @Override
  protected String getMyPropC() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropC ) ? props().getAny( Props.myPropC ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropC ) );
    }
  }

  @Override
  protected String getMyPropD() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myPropD ) ? props().getAny( Props.myPropD ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myPropD ) );
    }
  }

  @Override
  protected ReactNode[] getChildren() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.children ) ? props().getAny( Props.children ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.children ) );
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_RequiredChildrenWithManyOptional) this).dispose();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myPropA = React.shouldMinimizePropKeys() ? "a" : "myPropA";

    static final String myPropB = React.shouldMinimizePropKeys() ? "b" : "myPropB";

    static final String myPropC = React.shouldMinimizePropKeys() ? "c" : "myPropC";

    static final String myPropD = React.shouldMinimizePropKeys() ? "d" : "myPropD";

    static final String children = "children";
  }

  private static final class LiteNativeReactComponent extends NativeComponent {
    private React4j_RequiredChildrenWithManyOptional $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_RequiredChildrenWithManyOptional( this );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnComponentWillUnmount {
    private React4j_RequiredChildrenWithManyOptional $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_RequiredChildrenWithManyOptional( this );
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
