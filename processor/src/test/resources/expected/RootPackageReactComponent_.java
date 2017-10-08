import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import jsinterop.base.JsPropertyMapOfAny;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.ReactConfig;
import react4j.core.util.JsUtil;

@Generated("react4j.processor.ReactProcessor")
final class RootPackageReactComponent_ {
  public static final ComponentConstructorFunction<BaseProps, BaseState, React_RootPackageReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, React_RootPackageReactComponent> getConstructorFunction() {
    final JsPropertyMapOfAny prototype = JsUtil.getPrototypeForClass( React_RootPackageReactComponent.class );
    final ComponentConstructorFunction<BaseProps, BaseState, React_RootPackageReactComponent> componentConstructor = React_RootPackageReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "RootPackageReactComponent" );
    }
    return componentConstructor;
  }
}
