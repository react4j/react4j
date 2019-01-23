package com.example.arez.memoize;

import arez.Arez;
import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.Memoize;
import arez.annotations.Observe;
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
import react4j.internal.arez.IntrospectUtil;
import react4j.internal.arez.SchedulerUtil;

@ArezComponent(
    name = "ParameterizedReturnMemoizeComponent",
    disposeTrackable = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ParameterizedReturnMemoizeComponent extends ParameterizedReturnMemoizeComponent {
  React4j_ParameterizedReturnMemoizeComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ParameterizedReturnMemoizeComponent" );
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
    Disposable.dispose( this );
  }

  final void onRenderDepsChange() {
    onRenderDepsChange( false );
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
    clearRenderDepsChanged();
    SchedulerUtil.pauseUntilRenderLoopComplete();
    assert Disposable.isNotDisposed( this );
    final ReactNode result = super.render();
    if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() ) {
      Guards.invariant( () -> !getRenderObserver().getContext().getSpy().asObserverInfo( getRenderObserver() ).getDependencies().isEmpty(), () -> "ReactArezComponent render completed on '" + this + "' but the component does not have any Arez dependencies. This component should extend react4j.Component instead." );
    }
    return result;
  }

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
  <T> Consumer<T> getIcon(final String key) {
    return super.getIcon(key);
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ParameterizedReturnMemoizeComponent> implements OnComponentWillUnmount {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ParameterizedReturnMemoizeComponent createComponent() {
      return new Arez_React4j_ParameterizedReturnMemoizeComponent( this );
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_ParameterizedReturnMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ParameterizedReturnMemoizeComponent> implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ParameterizedReturnMemoizeComponent createComponent() {
      return new Arez_React4j_ParameterizedReturnMemoizeComponent( this );
    }

    @Override
    public final void componentDidMount() {
      ((React4j_ParameterizedReturnMemoizeComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_ParameterizedReturnMemoizeComponent) component() ).$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_ParameterizedReturnMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
