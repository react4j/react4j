import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "RootPackageCompleteComponent",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_RootPackageCompleteComponent extends RootPackageCompleteComponent {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_RootPackageCompleteComponent(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "RootPackageCompleteComponent" );
    }
    return viewConstructor;
  }

  @Override
  int getMyProp() {
    return $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myProp ).asInt();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myProp ), nextInputs.get( Inputs.myProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentDidMount() {
    postMount();
  }

  private void $$react4j$$_componentPreUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> prevInputs) {
    if ( null != prevInputs ) {
      final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
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
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidMount,
      OnComponentDidUpdate,
      OnShouldComponentUpdate,
      OnComponentWillUnmount,
      OnGetSnapshotBeforeUpdate {
    @NonNull
    private final React4j_RootPackageCompleteComponent view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_RootPackageCompleteComponent( this );
    }

    @Override
    public final void componentDidMount() {
      view.$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    @Nullable
    public final Object getSnapshotBeforeUpdate(final JsPropertyMap<@Nullable Object> prevInputs,
        final JsPropertyMap<@Nullable Object> prevState) {
      view.$$react4j$$_componentPreUpdate( prevInputs );
      return null;
    }

    @Override
    public final void componentDidUpdate(final JsPropertyMap<@Nullable Object> prevInputs) {
      view.$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentWillUnmount() {
      view.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
