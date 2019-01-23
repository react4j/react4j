package com.example.arez;

import arez.Arez;
import arez.Disposable;
import arez.Observer;
import arez.annotations.ArezComponent;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.Observe;
import arez.annotations.ObserverRef;
import arez.annotations.Priority;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.arez.IntrospectUtil;
import react4j.internal.arez.SchedulerUtil;

@ArezComponent(
    name = "ComponentFunctionalInterfaceProp",
    disposeTrackable = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ComponentFunctionalInterfaceProp extends ComponentFunctionalInterfaceProp {
  private boolean $$react4j$$_renderDepsChanged;

  private boolean $$react4j$$_unmounted;

  React4j_ComponentFunctionalInterfaceProp(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ComponentFunctionalInterfaceProp" );
    }
    return componentConstructor;
  }

  @Override
  protected ComponentFunctionalInterfaceProp.TestFunction getValue() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value ) ? props().getAny( Props.value ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value ) );
    }
  }

  private void $$react4j$$_componentDidMount() {
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentDidUpdate() {
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    $$react4j$$_unmounted = true;
    Disposable.dispose( this );
  }

  final void onRenderDepsChange() {
    if ( !$$react4j$$_renderDepsChanged ) {
      $$react4j$$_renderDepsChanged = true;
      if ( !$$react4j$$_unmounted ) {
        scheduleRender();
      }
    }
  }

  @Override
  @Nullable
  @Observe(
      name = "render",
      priority = Priority.LOW,
      executor = Executor.EXTERNAL,
      observeLowerPriorityDependencies = true,
      reportResult = false
  )
  protected ReactNode render() {
    $$react4j$$_renderDepsChanged = false;
    SchedulerUtil.pauseUntilRenderLoopComplete();
    assert Disposable.isNotDisposed( this );
    final ReactNode result = super.render();
    if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() ) {
      Guards.invariant( () -> !getRenderObserver().getContext().getSpy().asObserverInfo( getRenderObserver() ).getDependencies().isEmpty(), () -> "ReactArezComponent render completed on '" + this + "' but the component does not have any Arez dependencies. This component should extend react4j.Component instead." );
    }
    return result;
  }

  @Nonnull
  @ObserverRef
  abstract Observer getRenderObserver();

  @Override
  protected final void populateDebugData(@Nonnull final JsPropertyMap<Object> data) {
    if ( React.shouldStoreDebugDataAsState() && Arez.areSpiesEnabled() ) {
      IntrospectUtil.collectDependencyDebugData( getRenderObserver(), data );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String value = React.shouldMinimizePropKeys() ? "a" : "value";
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ComponentFunctionalInterfaceProp> implements OnComponentWillUnmount {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentFunctionalInterfaceProp createComponent() {
      return new Arez_React4j_ComponentFunctionalInterfaceProp( this );
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_ComponentFunctionalInterfaceProp) component() ).$$react4j$$_componentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ComponentFunctionalInterfaceProp> implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ComponentFunctionalInterfaceProp createComponent() {
      return new Arez_React4j_ComponentFunctionalInterfaceProp( this );
    }

    @Override
    public final void componentDidMount() {
      ((React4j_ComponentFunctionalInterfaceProp) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_ComponentFunctionalInterfaceProp) component() ).$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_ComponentFunctionalInterfaceProp) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
