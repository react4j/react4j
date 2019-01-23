import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnGetSnapshotBeforeUpdate;

@ArezComponent(
    name = "RootPackageCompleteComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_RootPackageCompleteComponent extends RootPackageCompleteComponent {
  React4j_RootPackageCompleteComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "RootPackageCompleteComponent" );
    }
    return componentConstructor;
  }

  @Override
  int getMyProp() {
    return props().getAny( Props.myProp ).asInt();
  }

  private void $$react4j$$_componentDidMount() {
    postMount();
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  private void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    if ( null != prevProps ) {
      final JsPropertyMap<Object> props = props();
      final boolean myProp = !Js.isTripleEqual( props.get( Props.myProp ), prevProps.get( Props.myProp ) );
      if ( myProp ) {
        onMyPropChange( props.getAny( Props.myProp ).asInt() );
      }
    }
    preUpdate();
  }

  private void $$react4j$$_componentDidUpdate() {
    postUpdate();
    if ( React.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<RootPackageCompleteComponent> implements OnComponentDidMount, OnComponentDidUpdate, OnGetSnapshotBeforeUpdate {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected RootPackageCompleteComponent createComponent() {
      return new Arez_React4j_RootPackageCompleteComponent( this );
    }

    @Override
    public final void componentDidMount() {
      ((React4j_RootPackageCompleteComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      ((React4j_RootPackageCompleteComponent) component() ).$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_RootPackageCompleteComponent) component() ).$$react4j$$_componentDidUpdate();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return ((React4j_RootPackageCompleteComponent) component() ).render();
    }
  }
}
