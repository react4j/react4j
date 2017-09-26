package react.arez;

import javax.annotation.Nonnull;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;

public abstract class ArezNativeComponent<P extends BaseProps, S extends BaseState>
  extends Component<P, S>
{
  protected ArezNativeComponent( @Nonnull final P props )
  {
    super( props );
  }
}
