package gwt.react_router.client;

import gwt.interop.utils.client.plainobjects.JsPlainObj;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "ReactRouter")
public class ReactRouter {

    public static HistoryMechanism browserHistory;
    public static HistoryMechanism hashHistory;

    static class RouterComponent extends Component<RouterProps, JsPlainObj>
    {
        public RouterComponent( final RouterProps props )
        {
            super( props );
        }

        @Override
        protected ReactElement<?, ?> render()
        {
            return null;
        }
    }

    static class Route extends Component<RouteProps,JsPlainObj>
    {
        public Route( final RouteProps props )
        {
            super( props );
        }

        @Override
        protected ReactElement<?, ?> render()
        {
            return null;
        }
    }

    public static JsConstructorFn<RouterComponent> Router;
    public static JsConstructorFn<Route> Route;
    public static JsConstructorFn<RouteProps> IndexRoute;
    public static JsConstructorFn<RedirectProps> Redirect;
    public static JsConstructorFn<RedirectProps> IndexRedirect;
}
