package com.example.prop;

import arez.Arez;
import arez.Disposable;
import arez.Observer;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.ComponentIdRef;
import arez.annotations.ComponentNameRef;
import arez.annotations.DepType;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.Observe;
import arez.annotations.ObserverRef;
import arez.annotations.Priority;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.IntrospectUtil;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.SchedulerUtil;
import react4j.internal.ViewConstructorFunction;
import react4j.internal.ViewState;

@ArezComponent(
    name = "com_example_prop_ImmutableDisposablePropModel",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutableDisposablePropModel extends ImmutableDisposablePropModel {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  @ComponentDependency(
      validateTypeAtRuntime = true
  )
  final Object $$react4j_immutable_input$$_value;

  private int $$react4j$$_state;

  private boolean $$react4j$$_scheduledDebugStateUpdate;

  React4j_ImmutableDisposablePropModel(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_value = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.value ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.value ).cast() : null;
    } else {
      $$react4j_immutable_input$$_value = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.value ) );
    }
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidateInputValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutableDisposablePropModel" );
    }
    return viewConstructor;
  }

  @Override
  Object getValue() {
    return $$react4j_immutable_input$$_value;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    return ViewState.SCHEDULED == $$react4j$$_state;
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
    ((Arez_React4j_ImmutableDisposablePropModel) this).dispose();
    $$react4j$$_state = ViewState.UNMOUNTED;
  }

  @Nullable
  @Observe(
      name = "render",
      priority = Priority.LOW,
      executor = Executor.EXTERNAL,
      depType = DepType.AREZ_OR_NONE,
      observeLowerPriorityDependencies = true,
      reportResult = false
  )
  ReactNode $$react4j$$_render() {
    $$react4j$$_state = ViewState.IDLE;
    assert Disposable.isNotDisposed( this );
    SchedulerUtil.pauseUntilRenderLoopComplete();
    final ReactNode result = render();
    if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() ) {
      Guards.invariant( () -> !$$react4j$$_getRenderObserver().getContext().getSpy().asObserverInfo( $$react4j$$_getRenderObserver() ).getDependencies().isEmpty(), () -> "View render completed on '" + this + "' without accessing any Arez dependencies but has a type set to TRACKING. The render method needs to access an Arez dependency or the type should be changed to STATEFUL or MAYBE_TRACKING." );
    }
    return result;
  }

  void onRenderDepsChange() {
    if ( ViewState.IDLE == $$react4j$$_state ) {
      $$react4j$$_state = ViewState.SCHEDULED;
      $$react4j$$_nativeView.forceUpdate();
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
      if ( IntrospectUtil.prepareStateUpdate( newState, $$react4j$$_nativeView.state() ) ) {
        $$react4j$$_nativeView.setState( newState );
        $$react4j$$_nativeView.forceUpdate();
        $$react4j$$_scheduledDebugStateUpdate = true;
      }
    }
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String value = React.shouldMinimizeInputKeys() ? "a" : "value";
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutableDisposablePropModel $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_ImmutableDisposablePropModel( this );
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

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidMount, OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutableDisposablePropModel $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_ImmutableDisposablePropModel( this );
    }

    @Override
    public final void componentDidMount() {
      if ( Disposable.isNotDisposed( $$react4j$$_view ) ) {
        $$react4j$$_view.$$react4j$$_componentDidMount();
      }
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
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevInputs) {
      if ( Disposable.isNotDisposed( $$react4j$$_view ) ) {
        $$react4j$$_view.$$react4j$$_componentDidUpdate();
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
