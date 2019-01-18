package com.example.on_prop_change;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.OnGetSnapshotBeforeUpdate;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_MultipleOnPropChange extends MultipleOnPropChange {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultipleOnPropChange" );
    }
    return componentConstructor;
  }

  @Override
  protected boolean getMyProp1() {
    return props().getAny( Props.myProp1 ).asBoolean();
  }

  @Override
  protected String getMyProp2() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.myProp2 ) ? props().getAny( Props.myProp2 ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.myProp2 ) );
    }
  }

  @Override
  protected int getMyProp3() {
    return props().getAny( Props.myProp3 ).asInt();
  }

  private void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    if ( null != prevProps ) {
      final JsPropertyMap<Object> props = props();
      final boolean myProp1 = !Js.isTripleEqual( props.get( Props.myProp1 ), prevProps.get( Props.myProp1 ) );
      final boolean myProp2 = !Js.isTripleEqual( props.get( Props.myProp2 ), prevProps.get( Props.myProp2 ) );
      final boolean myProp3 = !Js.isTripleEqual( props.get( Props.myProp3 ), prevProps.get( Props.myProp3 ) );
      if ( myProp1 && myProp2 && myProp3 ) {
        onPropChange( props.getAny( Props.myProp1 ).asBoolean(), Js.uncheckedCast( props.getAny( Props.myProp2 ) ), props.getAny( Props.myProp3 ).asInt() );
      }
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp1 = ReactConfig.shouldMinimizePropKeys() ? "a" : "myProp1";

    static final String myProp2 = ReactConfig.shouldMinimizePropKeys() ? "b" : "myProp2";

    static final String myProp3 = ReactConfig.shouldMinimizePropKeys() ? "c" : "myProp3";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultipleOnPropChange> implements OnGetSnapshotBeforeUpdate {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultipleOnPropChange createComponent() {
      return new React4j_MultipleOnPropChange();
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      ((React4j_MultipleOnPropChange) component() ).$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }
  }
}
