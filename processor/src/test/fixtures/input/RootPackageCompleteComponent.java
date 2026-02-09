import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.OnInputChange;
import react4j.annotations.PostMount;
import react4j.annotations.PostRender;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreRender;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.SortOrder;
import react4j.annotations.View;
import javax.annotation.Nullable;

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

  @Nullable
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

  @PostRender( sortOrder = SortOrder.APPLICATION + 50 )
  void postRender4()
  {
  }

  @PostRender( sortOrder = SortOrder.APPLICATION + 10 )
  void postRender2()
  {
  }

  @PostRender( sortOrder = SortOrder.APPLICATION + 20 )
  void postRender3()
  {
  }

  @PostRender( sortOrder = SortOrder.APPLICATION + 5 )
  void postRender1()
  {
  }

  @PreRender( sortOrder = SortOrder.APPLICATION + 50 )
  void preRender4()
  {
  }

  @PreRender( sortOrder = SortOrder.APPLICATION + 10 )
  void preRender2()
  {
  }

  @PreRender( sortOrder = SortOrder.APPLICATION + 20 )
  void preRender3()
  {
  }

  @PreRender( sortOrder = SortOrder.APPLICATION + 5 )
  void preRender1()
  {
  }
}
