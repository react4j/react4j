import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

@Generated("react.processor.ReactProcessor")
final class RootPackageReactComponent_ {
  public static final JsConstructorFn<React_RootPackageReactComponent> TYPE = getConstrutorFunction();

  @Nonnull
  private static JsConstructorFn<React_RootPackageReactComponent> getConstrutorFunction() {
    final JsConstructorFn<React_RootPackageReactComponent> constructorFn = JsConstructorFn.of( React_RootPackageReactComponent.class ) ;
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for RootPackageReactComponent defined by class RootPackageReactComponent" );
    assert null != constructorFn;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( constructorFn ).set( "displayName", "RootPackageReactComponent" );
    }
    return constructorFn;
  }
}
