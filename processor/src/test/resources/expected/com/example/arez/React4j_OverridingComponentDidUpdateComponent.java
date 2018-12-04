package com.example.arez;

import arez.Arez;
import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.Observe;
import arez.annotations.Priority;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;
import react4j.ReactNode;

@ArezComponent(
    name = "OverridingComponentDidUpdateComponent",
    disposeTrackable = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_OverridingComponentDidUpdateComponent extends OverridingComponentDidUpdateComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OverridingComponentDidUpdateComponent" );
    }
    return componentConstructor;
  }

  void $$react4j$$_componentDidMount() {
    if ( ReactConfig.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  final void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    postUpdate();
    if ( ReactConfig.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  final void $$react4j$$_componentWillUnmount() {
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
    pauseArezSchedulerUntilRenderLoopComplete();
    if ( Disposable.isDisposed( this ) ) {
      return null;
    }
    final ReactNode result = super.render();
    if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() ) {
      Guards.invariant( () -> !getContext().getSpy().asObserverInfo( getRenderObserver() ).getDependencies().isEmpty(), () -> "ReactArezComponent render completed on '" + this + "' but the component does not have any Arez dependencies. This component should extend react4j.Component instead." );
    }
    return result;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);

    void componentWillUnmount();
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);

    void componentWillUnmount();
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<OverridingComponentDidUpdateComponent> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected OverridingComponentDidUpdateComponent createComponent() {
      return new Arez_React4j_OverridingComponentDidUpdateComponent();
    }

    @Override
    public final void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps) {
      ((Arez_React4j_OverridingComponentDidUpdateComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((Arez_React4j_OverridingComponentDidUpdateComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<OverridingComponentDidUpdateComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected OverridingComponentDidUpdateComponent createComponent() {
      return new Arez_React4j_OverridingComponentDidUpdateComponent();
    }

    @Override
    public final void componentDidMount() {
      ((Arez_React4j_OverridingComponentDidUpdateComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps) {
      ((Arez_React4j_OverridingComponentDidUpdateComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((Arez_React4j_OverridingComponentDidUpdateComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
