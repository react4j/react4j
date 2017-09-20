package gwt.react.client.components;

import gwt.interop.utils.client.plainobjects.JsPlainObj;
import gwt.interop.utils.shared.JsHelper;
import gwt.react.client.GwtReactConfig;
import gwt.react.client.proptypes.BaseProps;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility functions for working with ES6 style React.Components
 */
public class ComponentUtils
{
  private ComponentUtils()
  {
  }

  private static final Map<Class<?>, ComponentConstructorFn<?>> _constructors = new HashMap<>();

  /**
   * Given the Class of a JsType annotated {@link Component} class, return the constructor function to use in Javascript
   *
   * @param cls The Class
   * @param <P> The type of props the {@link Component} supports
   * @param <S> The type of state the {@link Component} supports
   * @param <T> The type of {@link Component}
   * @return The constructor function
   */
  @SuppressWarnings( "unchecked" )
  public static <
    P extends BaseProps,
    S extends JsPlainObj,
    T extends Component<P, S>
    >
  ComponentConstructorFn<P> getCtorFn( Class<T> cls )
  {
    if ( GwtReactConfig.enableComponentNames() )
    {
      ComponentConstructorFn<P> constructorFn = (ComponentConstructorFn<P>) _constructors.get( cls );
      if ( null == constructorFn )
      {
        constructorFn = getCtorFn0( cls );
        JsHelper.setObjectProperty( constructorFn, "displayName", cls.getSimpleName() );
        _constructors.put( cls, constructorFn );
      }
      return constructorFn;
    }
    else
    {
      return getCtorFn0( cls );
    }
  }

  /**
   * Given the Class of a JsType annotated {@link Component} class, return the constructor function to use in Javascript
   *
   * @param cls The Class
   * @param <P> The type of props the {@link Component} supports
   * @param <S> The type of state the {@link Component} supports
   * @param <T> The type of {@link Component}
   * @return The constructor function
   */
  public static native <P extends BaseProps, S extends JsPlainObj, T extends Component<P, S>> ComponentConstructorFn<P> getCtorFn0(
    Class<T> cls ) /*-{
    return cls.@java.lang.Class::jsConstructor;
  }-*/;
}
