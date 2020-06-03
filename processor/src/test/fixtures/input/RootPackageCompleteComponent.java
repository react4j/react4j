import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class RootPackageCompleteComponent
{
  @PropDefault( name = "myProp" )
  static final int MY_PROP = 37;

  @OnPropChange
  void onMyPropChange( int myProp )
  {
  }

  @Prop
  abstract int getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }

  @PostMount
  void postMount()
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
