package com.example.prop;

import arez.Arez;
import arez.Disposable;
import arez.Observer;
import arez.annotations.ArezComponent;
import arez.annotations.DepType;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.InjectMode;
import arez.annotations.Observe;
import arez.annotations.ObserverRef;
import arez.annotations.Priority;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeComponent;
import react4j.internal.arez.ComponentState;
import react4j.internal.arez.IntrospectUtil;
import react4j.internal.arez.SchedulerUtil;

@ArezComponent(
    name = "ImplicitDisposableProp",
    disposeNotifier = Feature.DISABLE,
    inject = InjectMode.NONE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_ImplicitDisposableProp extends ImplicitDisposableProp {
  private int $$react4j$$_state;

  React4j_ImplicitDisposableProp(@Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    bindComponent( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ImplicitDisposableProp" );
    }
    return componentConstructor;
  }

  @Override
  protected ImplicitDisposableProp.Model getModel() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAsAny( Props.model ) ? props().getAsAny( Props.model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAsAny( Props.model ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.model ), nextProps.get( Props.model ) ) ) {
      return true;
    }
    return ComponentState.SCHEDULED == $$react4j$$_state;
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
    ((Arez_React4j_ImplicitDisposableProp) this).dispose();
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
    final ImplicitDisposableProp.Model $$react4jv$$_getModel = getModel();
    if ( Disposable.isDisposed( $$react4jv$$_getModel ) ) {
      return null;
    }
    final ReactNode result = super.render();
    if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() ) {
      Guards.invariant( () -> !getRenderObserver().getContext().getSpy().asObserverInfo( getRenderObserver() ).getDependencies().isEmpty(), () -> "Component render completed on '" + this + "' without accessing any Arez dependencies but has a type set to TRACKING. The render method needs to access an Arez dependency or the type should be changed to STATEFUL or MAYBE_TRACKING." );
    }
    return result;
  }

  final void onRenderDepsChange() {
    if ( ComponentState.IDLE == $$react4j$$_state ) {
      $$react4j$$_state = ComponentState.SCHEDULED;
      scheduleRender();
    }
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
    static final String model = React.shouldMinimizePropKeys() ? "a" : "model";
  }

  private static final class LiteNativeReactComponent extends NativeComponent {
    private React4j_ImplicitDisposableProp $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImplicitDisposableProp( this );
    }

    @JsMethod
    protected final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @JsMethod
    protected final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @JsMethod
    @Nullable
    protected final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent {
    private React4j_ImplicitDisposableProp $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_ImplicitDisposableProp( this );
    }

    @JsMethod
    protected final void componentDidMount() {
      $$react4j$$_component.$$react4j$$_componentDidMount();
    }

    @JsMethod
    protected final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @JsMethod
    protected final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      $$react4j$$_component.$$react4j$$_componentDidUpdate();
    }

    @JsMethod
    protected final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @JsMethod
    @Nullable
    protected final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
