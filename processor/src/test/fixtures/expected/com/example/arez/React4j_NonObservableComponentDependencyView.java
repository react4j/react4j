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
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
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

@NullMarked
@SuppressArezWarnings("Arez:UnnecessaryDefault")
@ArezComponent(
    name = "com_example_arez_NonObservableComponentDependencyView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_NonObservableComponentDependencyView extends NonObservableComponentDependencyView {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  private int $$react4j$$_state;

  private boolean $$react4j$$_scheduledDebugStateUpdate;

  React4j_NonObservableComponentDependencyView(
      final react4j.internal.NativeView $$react4j$$_nativeView,
      final NonObservableComponentDependencyView.Model model) {
    super( model );
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidateInputValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "NonObservableComponentDependency" );
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$model = inputs.get( Inputs.model );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$model, () -> "Required input named 'model' is missing from view named 'NonObservableComponentDependencyView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
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
    ((Arez_React4j_NonObservableComponentDependencyView) this).dispose();
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
      final var newState = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast( JsPropertyMap.of() );
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
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String model = React.shouldMinimizeInputKeys() ? "a" : "model";

    private Inputs() {
    }
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nullable
    private final React4j_NonObservableComponentDependencyView view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      assert null != inputs;
      final NonObservableComponentDependencyView.Model model = inputs.getAsAny( Inputs.model ).cast();
      view = Disposable.isDisposed( inputs.getAsAny( Inputs.model ).cast() ) ? null : new Arez_React4j_NonObservableComponentDependencyView( this, model );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_NonObservableComponentDependencyView.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_shouldComponentUpdate( nextInputs );
      } else {
        return false;
      }
    }

    @Override
    public final void componentWillUnmount() {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    @Nullable
    public final ReactNode render() {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidMount,
      OnComponentDidUpdate,
      OnShouldComponentUpdate,
      OnComponentWillUnmount {
    @Nullable
    private final React4j_NonObservableComponentDependencyView view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      assert null != inputs;
      final NonObservableComponentDependencyView.Model model = inputs.getAsAny( Inputs.model ).cast();
      view = Disposable.isDisposed( inputs.getAsAny( Inputs.model ).cast() ) ? null : new Arez_React4j_NonObservableComponentDependencyView( this, model );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_NonObservableComponentDependencyView.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final void componentDidMount() {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentDidMount();
      }
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_shouldComponentUpdate( nextInputs );
      } else {
        return false;
      }
    }

    @Override
    public final void componentDidUpdate(final JsPropertyMap<@Nullable Object> prevInputs) {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentDidUpdate();
      }
    }

    @Override
    public final void componentWillUnmount() {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    @Nullable
    public final ReactNode render() {
      if ( null != view && Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }
}
