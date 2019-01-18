package com.example.arez.memoize;

import arez.Arez;
import arez.Disposable;
import arez.ObservableValue;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Executor;
import arez.annotations.Feature;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import arez.annotations.ObservableValueRef;
import arez.annotations.Observe;
import arez.annotations.Priority;
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
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;

@ArezComponent(
    name = "PropAndMemoizeComponent",
    disposeTrackable = Feature.DISABLE
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_PropAndMemoizeComponent extends PropAndMemoizeComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "PropAndMemoizeComponent" );
    }
    return componentConstructor;
  }

  @Override
  @Observable(
      name = "value",
      expectSetter = false,
      readOutsideTransaction = true
  )
  protected String getValue() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.value ) ? props().getAny( Props.value ).asString() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.value ) );
    }
  }

  @Nonnull
  @ObservableValueRef
  protected abstract ObservableValue getValueObservableValue();

  @Action(
      verifyRequired = false
  )
  boolean $$react4j$$_shouldComponentUpdate(@Nullable final JsPropertyMap<Object> nextProps) {
    final JsPropertyMap<Object> props = props();
    boolean modified = false;
    assert null != nextProps;
    if ( !Js.isTripleEqual( props.get( Props.value ), nextProps.get( Props.value ) ) ) {
      getValueObservableValue().reportChanged();
      modified = true;
    }
    return modified || hasRenderDepsChanged();
  }

  private void $$react4j$$_componentDidMount() {
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    Disposable.dispose( this );
  }

  final void onRenderDepsChange() {
    onRenderDepsChange( true );
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
    assert Disposable.isNotDisposed( this );
    final ReactNode result = super.render();
    if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() ) {
      Guards.invariant( () -> !getContext().getSpy().asObserverInfo( getRenderObserver() ).getDependencies().isEmpty(), () -> "ReactArezComponent render completed on '" + this + "' but the component does not have any Arez dependencies. This component should extend react4j.Component instead." );
    }
    return result;
  }

  @Override
  @Memoize(
      priority = Priority.LOWEST
  )
  public boolean isActive() {
    return super.isActive();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String value = React.shouldMinimizePropKeys() ? "a" : "value";
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<PropAndMemoizeComponent> implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropAndMemoizeComponent createComponent() {
      return new Arez_React4j_PropAndMemoizeComponent();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return ((React4j_PropAndMemoizeComponent) component() ).$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_PropAndMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<PropAndMemoizeComponent> implements OnComponentDidMount, OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected PropAndMemoizeComponent createComponent() {
      return new Arez_React4j_PropAndMemoizeComponent();
    }

    @Override
    public final void componentDidMount() {
      ((React4j_PropAndMemoizeComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return ((React4j_PropAndMemoizeComponent) component() ).$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_PropAndMemoizeComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_PropAndMemoizeComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
