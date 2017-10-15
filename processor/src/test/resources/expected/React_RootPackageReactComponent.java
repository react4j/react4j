import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_RootPackageReactComponent extends NativeAdapterComponent<BaseProps, BaseState, RootPackageReactComponent> implements RootPackageReactComponentLifecycle {
  @JsConstructor
  React_RootPackageReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected RootPackageReactComponent createComponent() {
    return new RootPackageReactComponent_();
  }
}
