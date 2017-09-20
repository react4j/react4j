package gwt.react.todo_mvc.client;

import gwt.interop.utils.client.plainobjects.JsPlainObj;
import gwt.interop.utils.shared.collections.StringMap;
import gwt.react.client.components.Component;
import gwt.react.client.components.ComponentConstructorFn;
import gwt.react.client.components.ComponentUtils;
import gwt.react.client.proptypes.BaseProps;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class SideComponent<P extends BaseProps, S extends JsPlainObj>
{
  @Nonnull
  private final Component<P, S> _component;

  protected SideComponent( @Nonnull final Component<P, S> component )
  {
    _component = Objects.requireNonNull( component );
  }

  @Nonnull
  protected Component<P, S> component()
  {
    return _component;
  }

  @Nonnull
  protected S state()
  {
    return component().state();
  }

  @Nonnull
  protected P props()
  {
    return component().props();
  }

  @Nonnull
  protected StringMap<Object> refs()
  {
    return component().refs();
  }

  protected void setInitialState( @Nonnull final S state )
  {
    component().setInitialState( state );
  }

  protected static <
    P extends BaseProps,
    S extends JsPlainObj,
    T extends Component<P, S>
    >
  ComponentConstructorFn<P> getCtor( @Nonnull final Class<T> type )
  {
    return ComponentUtils.getCtorFn( type );
  }
}
