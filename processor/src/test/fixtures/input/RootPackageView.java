import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class RootPackageView
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
