package com.example.arez.memoize;

import arez.Arez;
import arez.Disposable;
import arez.Observer;
import arez.annotations.ArezComponent;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.Memoize;
import arez.annotations.Observe;
import arez.annotations.ObserverRef;
import arez.annotations.Priority;
import java.util.function.Consumer;
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
import react4j.internal.arez.ComponentState;
import react4j.internal.arez.IntrospectUtil;
import react4j.internal.arez.SchedulerUtil;

@ArezComponent(
    name = "GenericsReturnMemoizeComponent",
    disposeTrackable = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_GenericsReturnMemoizeComponent extends GenericsReturnMemoizeComponent {
  private int $$react4j$$_state;

  React4j_GenericsReturnMemoizeComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "GenericsReturnMemoizeComponent" );
    }
    return componentConstructor;
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
    $$react4j$$_state = ComponentState.UNMOUNTED;
    Disposable.dispose( this );
  }

  final void onRenderDepsChange() {
    if ( ComponentState.IDLE == $$react4j$$_state ) {
      $$react4j$$_state = ComponentState.SCHEDULED;
      scheduleRender();
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
    $$react4j$$_state = ComponentState.IDLE;
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

  @Override
  @Memoize(
      priority = Priority.LOWEST
  )
  Consumer<String> getIcon(final String key) {
    return super.getIcon(key);
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<GenericsReturnMemoizeComponent> implements OnComponentWillUnmount {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericsReturnMemoizeComponent createComponent() {
      return new Arez_React4j_GenericsReturnMemoizeComponent( this );
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_GenericsReturnMemoizeComponent) component() ).render();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<GenericsReturnMemoizeComponent> implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected GenericsReturnMemoizeComponent createComponent() {
      return new Arez_React4j_GenericsReturnMemoizeComponent( this );
    }

    @Override
    public final void componentDidMount() {
      ((React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_GenericsReturnMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_GenericsReturnMemoizeComponent) component() ).render();
    }
  }
}
