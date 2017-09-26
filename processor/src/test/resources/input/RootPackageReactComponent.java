import javax.annotation.Nonnull;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;
import react.core.SideComponent;

@ReactComponent
class RootPackageReactComponent
  extends SideComponent<BaseProps, BaseState>
{
  RootPackageReactComponent( @Nonnull final Component<BaseProps, BaseState> component )
  {
    super( component );
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
