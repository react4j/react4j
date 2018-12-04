package com.example.prop;

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
    name = "ImplicitDisposableProp",
    disposeTrackable = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ImplicitDisposableProp extends ImplicitDisposableProp {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ImplicitDisposableProp" );
    }
    return componentConstructor;
  }

  @Override
  protected ImplicitDisposableProp.Model getModel() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.model ) ? props().getAny( Props.model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.model ) );
    }
  }

  void $$react4j$$_componentDidMount() {
    if ( ReactConfig.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  final void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
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
    final ImplicitDisposableProp.Model $$react4jv$$_getModel = getModel();
    if ( Disposable.isDisposed( $$react4jv$$_getModel ) ) {
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
    static final String model = ReactConfig.shouldMinimizePropKeys() ? "a" : "model";
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
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

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ImplicitDisposableProp> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ImplicitDisposableProp createComponent() {
      return new Arez_React4j_ImplicitDisposableProp();
    }

    @Override
    public final void componentWillUnmount() {
      ((Arez_React4j_ImplicitDisposableProp) component() ).$$react4j$$_componentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ImplicitDisposableProp> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ImplicitDisposableProp createComponent() {
      return new Arez_React4j_ImplicitDisposableProp();
    }

    @Override
    public final void componentDidMount() {
      ((Arez_React4j_ImplicitDisposableProp) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps) {
      ((Arez_React4j_ImplicitDisposableProp) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((Arez_React4j_ImplicitDisposableProp) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
