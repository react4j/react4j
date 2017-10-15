import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class RootPackageReactComponent_ extends RootPackageReactComponent {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_RootPackageReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_RootPackageReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, React_RootPackageReactComponent> componentConstructor = React_RootPackageReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "RootPackageReactComponent" );
    }
    return componentConstructor;
  }
}
