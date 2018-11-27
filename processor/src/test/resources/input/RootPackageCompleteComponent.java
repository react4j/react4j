import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class RootPackageCompleteComponent
  extends Component
{
  @PropDefault( name = "myProp" )
  static final int MY_PROP = 37;

  @OnPropChange
  void onMyPropChange( int myProp )
  {
  }

  @Prop
  abstract int getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Override
  protected void componentDidMount()
  {
  }

  @PreUpdate
  void preUpdate()
  {
  }

  @PostUpdate
  void postUpdate()
  {
  }
}
