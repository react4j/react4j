package com.example.arez;

import arez.Arez;
import arez.Disposable;
import arez.Observer;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentIdRef;
import arez.annotations.ComponentNameRef;
import arez.annotations.DepType;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.Observe;
import arez.annotations.ObserverRef;
import arez.annotations.Priority;
import elemental2.core.JsObject;
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
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.arez.ComponentState;
import react4j.internal.arez.IntrospectUtil;
import react4j.internal.arez.SchedulerUtil;

@ArezComponent(
    name = "BasicArezReactComponent",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_BasicArezReactComponent extends BasicArezReactComponent {
  private int $$react4j$$_state;

  private boolean $$react4j$$_scheduledDebugStateUpdate;

  React4j_BasicArezReactComponent(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicArezReactComponent" );
    }
    return componentConstructor;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    return ComponentState.SCHEDULED == $$react4j$$_state;
  }

  private void $$react4j$$_componentDidMount() {
    if ( React.shouldStoreDebugDataAsState() && Arez.areSpiesEnabled() ) {
      $$react4j$$_storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentDidUpdate() {
    if ( React.shouldStoreDebugDataAsState() && Arez.areSpiesEnabled() ) {
      $$react4j$$_storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    $$react4j$$_state = ComponentState.UNMOUNTED;
    ((Arez_React4j_BasicArezReactComponent) this).dispose();
  }

  @Override
  @Nullable
  @Observe(
      name = "render",
      priority = Priority.LOW,
      executor = Executor.EXTERNAL,
      depType = DepType.AREZ_OR_NONE,
      observeLowerPriorityDependencies = true,
      reportResult = false
  )
  protected ReactNode render() {
    $$react4j$$_state = ComponentState.IDLE;
    SchedulerUtil.pauseUntilRenderLoopComplete();
    assert Disposable.isNotDisposed( this );
    final ReactNode result = super.render();
    if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() ) {
      Guards.invariant( () -> !$$react4j$$_getRenderObserver().getContext().getSpy().asObserverInfo( $$react4j$$_getRenderObserver() ).getDependencies().isEmpty(), () -> "Component render completed on '" + this + "' without accessing any Arez dependencies but has a type set to TRACKING. The render method needs to access an Arez dependency or the type should be changed to STATEFUL or MAYBE_TRACKING." );
    }
    return result;
  }

  void onRenderDepsChange() {
    if ( ComponentState.IDLE == $$react4j$$_state ) {
      $$react4j$$_state = ComponentState.SCHEDULED;
      component().forceUpdate();
    }
  }

  @Nonnull
  @ObserverRef(
      name = "render"
  )
  abstract Observer $$react4j$$_getRenderObserver();

  @ComponentIdRef
  abstract int $$react4j$$_getComponentId();

  @ComponentNameRef
  abstract String $$react4j$$_getComponentName();

  private void $$react4j$$_storeDebugDataAsState() {
    if ( $$react4j$$_scheduledDebugStateUpdate ) {
      $$react4j$$_scheduledDebugStateUpdate = false;
    } else {
      final JsPropertyMap<Object> newState = JsPropertyMap.of();
      newState.set( "Arez.id", $$react4j$$_getComponentId() );
      newState.set( "Arez.name", $$react4j$$_getComponentName() );
      IntrospectUtil.collectDependencyDebugData( $$react4j$$_getRenderObserver(), newState );
      if ( IntrospectUtil.prepareStateUpdate( newState, component().state() ) ) {
        component().setState( Js.cast( JsObject.freeze( newState ) ) );
        component().forceUpdate();
        $$react4j$$_scheduledDebugStateUpdate = true;
      }
    }
  }

  static final class Factory {
    @Nonnull
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_BasicArezReactComponent $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_BasicArezReactComponent( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
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

  private static final class NativeReactComponent extends NativeComponent implements OnComponentDidMount, OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_BasicArezReactComponent $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_BasicArezReactComponent( this );
    }

    @Override
    public final void componentDidMount() {
      $$react4j$$_component.$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      $$react4j$$_component.$$react4j$$_componentDidUpdate();
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
