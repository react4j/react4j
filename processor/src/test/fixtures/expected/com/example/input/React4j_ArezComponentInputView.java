package com.example.input;

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
import org.jspecify.annotations.NonNull;
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
    name = "com_example_input_ArezComponentInputView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ArezComponentInputView extends ArezComponentInputView {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  private int $$react4j$$_state;

  private boolean $$react4j$$_scheduledDebugStateUpdate;

  React4j_ArezComponentInputView(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidateInputValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ArezComponentInput" );
    }
    return viewConstructor;
  }

  @Override
  @Nullable String getValue() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.value ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.value ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.value ) );
    }
  }

  @Override
  ArezComponentInputView. @Nullable Model getModel() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.model ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.model ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.model ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.value ), nextInputs.get( Inputs.value ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.model ), nextInputs.get( Inputs.model ) ) ) {
      return true;
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
    ((Arez_React4j_ArezComponentInputView) this).dispose();
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
    static final String value = React.shouldMinimizeInputKeys() ? "a" : "value";

    static final String model = React.shouldMinimizeInputKeys() ? "b" : "model";

    private Inputs() {
    }
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @NonNull
    private final React4j_ArezComponentInputView view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_ArezComponentInputView( this );
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
    @NonNull
    private final React4j_ArezComponentInputView view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_ArezComponentInputView( this );
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
