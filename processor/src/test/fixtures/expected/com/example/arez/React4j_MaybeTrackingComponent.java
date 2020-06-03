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
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
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
    name = "MaybeTrackingComponent",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MaybeTrackingComponent extends MaybeTrackingComponent {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  private int $$react4j$$_state;

  private boolean $$react4j$$_scheduledDebugStateUpdate;

  React4j_MaybeTrackingComponent(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "MaybeTrackingComponent" );
    }
    return viewConstructor;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
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
    $$react4j$$_state = ViewState.UNMOUNTED;
    ((Arez_React4j_MaybeTrackingComponent) this).dispose();
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
    SchedulerUtil.pauseUntilRenderLoopComplete();
    assert Disposable.isNotDisposed( this );
    return render();
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
        $$react4j$$_nativeView.setState( Js.cast( JsObject.freeze( newState ) ) );
        $$react4j$$_nativeView.forceUpdate();
        $$react4j$$_scheduledDebugStateUpdate = true;
      }
    }
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_MaybeTrackingComponent $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_MaybeTrackingComponent( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_view.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidMount, OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_MaybeTrackingComponent $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_view = new Arez_React4j_MaybeTrackingComponent( this );
    }

    @Override
    public final void componentDidMount() {
      $$react4j$$_view.$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      $$react4j$$_view.$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_view.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }
}
