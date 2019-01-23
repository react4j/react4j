package react4j.arez;

import arez.Observer;
import arez.annotations.Action;
import arez.annotations.ObserverRef;
import arez.annotations.PreDispose;
import javax.annotation.Nonnull;
import react4j.Component;

/**
 * A base class for all Arez enabled components. This class makes the component
 * rendering reactive and it will schedule a re-render any time any of the observable
 * entities accessed within the scope of the render method are changed.
 *
 * <p>To achieve this goal, the props and state of the component are converted into
 * observable properties. This of course means they must be accessed within the scope
 * of an Arez transaction. (Typically this means it needs to be accessed within the
 * scope of a {@link Action} annotated method or within the scope of the render method.</p>
 */
public abstract class ReactArezComponent
  extends Component
{
  private boolean _renderDepsChanged;
  private boolean _unmounted;

  /**
   * Return true if the render dependencies have been marked as changed and component has yet to be re-rendered.
   *
   * @return true if render dependencies changed, false otherwise.
   */
  protected final boolean hasRenderDepsChanged()
  {
    return _renderDepsChanged;
  }

  /**
   * Helper method invoked when it has detected a dependency of the render method has changed.
   *
   * @param componentHasObservableProps flag indicating whether the component has any observable props.
   */
  protected final void onRenderDepsChange( final boolean componentHasObservableProps )
  {
    if ( !_renderDepsChanged )
    {
      _renderDepsChanged = true;
      if ( !_unmounted )
      {
        scheduleRender( !componentHasObservableProps );
      }
    }
  }

  protected final void clearRenderDepsChanged()
  {
    _renderDepsChanged = false;
  }

  /**
   * Return the Observer associated with the render tracker method.
   *
   * @return the Observer associated with the render tracker method.
   */
  @ObserverRef
  @Nonnull
  protected abstract Observer getRenderObserver();

  @PreDispose
  protected void preDispose()
  {
    _unmounted = true;
  }
}
