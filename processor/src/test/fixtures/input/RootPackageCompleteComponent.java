import react4j.ReactNode;
import react4j.annotations.OnInputChange;
import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class RootPackageCompleteComponent
{
  @InputDefault( name = "myProp" )
  static final int MY_PROP = 37;

  @OnInputChange
  void onMyPropChange( int myProp )
  {
  }

  @Input
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
