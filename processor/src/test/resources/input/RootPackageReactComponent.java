import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;
import react.core.SideComponent;

@ReactComponent
class RootPackageReactComponent
  extends SideComponent<BaseProps, BaseState>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
