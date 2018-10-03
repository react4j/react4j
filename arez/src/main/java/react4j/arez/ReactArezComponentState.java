package react4j.arez;

final class ReactArezComponentState
{
  private static int c_nextComponentId = 1;
  private final int _arezComponentId;
  private boolean _renderDepsChanged;
  private boolean _unmounted;
  /**
   * If the last render resulted in state update to record new arez state then this will be true.
   * It guards against multiple renders of a single component where rendering is just updating
   * react state. Otherwise dependencies on values that are never equal (i.e. Streams) will result
   * in infinite re-renders ultimately triggering invariant failure from React.
   *
   * This should only be true if ReactArezConfig.shouldStoreArezDataAsState() returns true.
   */
  private boolean _scheduledArezStateUpdate;

  ReactArezComponentState()
  {
    _arezComponentId = c_nextComponentId++;
  }

  /**
   * Return true if the render dependencies have been marked as changed and component has yet to be re-rendered.
   *
   * @return true if render dependencies changed, false otherwise.
   */
  boolean hasRenderDepsChanged()
  {
    return _renderDepsChanged;
  }

  /**
   * Hook used by Arez to notify component that it needs to be re-rendered.
   *
   * @return true if component should schedule render, false otherwise.
   */
  boolean onRenderDepsChanged()
  {
    if ( !_renderDepsChanged )
    {
      _renderDepsChanged = true;
      return !_unmounted;
    }
    else
    {
      return false;
    }
  }

  /**
   * Return the unique identifier of component according to Arez.
   *
   * @return the unique identifier of component according to Arez.
   */
  int getArezComponentId()
  {
    return _arezComponentId;
  }

  protected void onRender()
  {
    _renderDepsChanged = false;
  }

  void onUnmount()
  {
    _unmounted = true;
  }

  boolean isScheduledArezStateUpdate()
  {
    return _scheduledArezStateUpdate;
  }

  void setScheduledArezStateUpdate( final boolean scheduledArezStateUpdate )
  {
    _scheduledArezStateUpdate = scheduledArezStateUpdate;
  }
}
