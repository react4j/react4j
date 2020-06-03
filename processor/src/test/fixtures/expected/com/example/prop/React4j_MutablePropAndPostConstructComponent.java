package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentStateRef;
import arez.annotations.Feature;
import java.util.Objects;
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
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "MutablePropAndPostConstructComponent",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MutablePropAndPostConstructComponent extends MutablePropAndPostConstructComponent {
  @Nonnull
  private final NativeComponent $$react4j$$_nativeComponent;

  React4j_MutablePropAndPostConstructComponent(
      @Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    this.$$react4j$$_nativeComponent = Objects.requireNonNull( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MutablePropAndPostConstructComponent" );
    }
    return componentConstructor;
  }

  @ComponentStateRef
  abstract boolean $$react4j$$_isReady();

  @Override
  String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> $$react4j$$_isReady(), () -> "The component '" + this + "' accessed the prop named 'myProp' before the component is ready (possibly in a @PostConstruct annotated method?) and does not have a @OnPropChange annotated method to cover the prop and reflect changes of the prop onto the component. This is considered a likely bug and the @Prop should be made immutable or an @OnPropChange method added to cover the prop. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:MutablePropAccessedInPostConstruct\" ) or @SuppressReact4jWarnings( \"React4j:MutablePropAccessedInPostConstruct\" ) to the @Prop annotated method." );
    }
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeComponent.props().getAsAny( Props.myProp ) ? $$react4j$$_nativeComponent.props().getAsAny( Props.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeComponent.props().getAsAny( Props.myProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    final JsPropertyMap<Object> props = $$react4j$$_nativeComponent.props();
    if ( !Js.isTripleEqual( props.get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MutablePropAndPostConstructComponent) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_MutablePropAndPostConstructComponent $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_MutablePropAndPostConstructComponent( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.$$react4j$$_render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_MutablePropAndPostConstructComponent $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_MutablePropAndPostConstructComponent( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.$$react4j$$_render();
    }
  }
}
