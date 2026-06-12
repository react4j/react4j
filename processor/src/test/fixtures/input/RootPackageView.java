import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View( type = View.Type.STATEFUL )
abstract class RootPackageView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
