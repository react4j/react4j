import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "RootPackageCompleteComponent",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_RootPackageCompleteComponent extends RootPackageCompleteComponent {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_RootPackageCompleteComponent(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "RootPackageCompleteComponent" );
    }
    return viewConstructor;
  }

  @Override
  int getMyProp() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ).asInt();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myProp ), nextInputs.get( Inputs.myProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentDidMount() {
    postMount();
  }

  private void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevInputs) {
    if ( null != prevInputs ) {
      final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
      final boolean myProp = !Js.isTripleEqual( inputs.get( Inputs.myProp ), prevInputs.get( Inputs.myProp ) );
      if ( myProp ) {
        onMyPropChange( inputs.getAsAny( Inputs.myProp ).asInt() );
      }
    }
    preUpdate();
  }

  private void $$react4j$$_componentDidUpdate() {
    postUpdate();
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_RootPackageCompleteComponent) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    preRender1();
    preRender2();
    preRender3();
    preRender4();
    final ReactNode result = render();
    postRender1();
    postRender2();
    postRender3();
    postRender4();
    return result;
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidMount, OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount, OnGetSnapshotBeforeUpdate {
    @Nonnull
    private final React4j_RootPackageCompleteComponent $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_RootPackageCompleteComponent( this );
    }

    @Override
    public final void componentDidMount() {
      $$react4j$$_view.$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevInputs,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_view.$$react4j$$_componentPreUpdate( prevInputs );
      return null;
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevInputs) {
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
