import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
final class React_RootPackageReactComponent extends NativeAdapterComponent<BaseProps, BaseState, RootPackageReactComponent> {
  @JsConstructor
  private React_RootPackageReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected RootPackageReactComponent createComponent() {
    return new RootPackageReactComponent();
  }
}
