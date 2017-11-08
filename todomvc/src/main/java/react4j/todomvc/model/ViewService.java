package react4j.todomvc.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.arez.Disposable;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.Autorun;
import org.realityforge.arez.annotations.Observable;
import org.realityforge.arez.browser.extras.BrowserLocation;

@ArezComponent( nameIncludesId = false )
public class ViewService
{
  @Nonnull
  private final BrowserLocation _browserLocation;
  @Nullable
  private Todo _todoBeingEdited;
  @Nonnull
  private FilterMode _filterMode = FilterMode.ALL;

  ViewService( @Nonnull final BrowserLocation browserLocation )
  {
    _browserLocation = Objects.requireNonNull( browserLocation );
  }

  @Observable
  @Nullable
  public Todo getTodoBeingEdited()
  {
    return _todoBeingEdited;
  }

  public void setTodoBeingEdited( @Nullable final Todo todoBeingEdited )
  {
    _todoBeingEdited = todoBeingEdited;
  }

  @Observable
  @Nonnull
  public FilterMode getFilterMode()
  {
    return _filterMode;
  }

  public void setFilterMode( @Nonnull final FilterMode filterMode )
  {
    _filterMode = Objects.requireNonNull( filterMode );
  }

  @Autorun
  void updateTodoBeingEdited()
  {
    final Todo todoBeingEdited = getTodoBeingEdited();
    if ( null != todoBeingEdited && Disposable.isDisposed( todoBeingEdited ) )
    {
      setTodoBeingEdited( null );
    }
  }

  @Autorun
  void updateFilterMode()
  {
    cleanLocation();

    final String location = _browserLocation.getLocation();
    if ( isValid( location ) )
    {
      if ( "active".equals( location ) )
      {
        setFilterMode( FilterMode.ACTIVE );
      }
      else if ( "completed".equals( location ) )
      {
        setFilterMode( FilterMode.COMPLETED );
      }
      else
      {
        setFilterMode( FilterMode.ALL );
      }
    }
  }

  private void cleanLocation()
  {
    final String browserLocation = _browserLocation.getBrowserLocation();
    if ( isValid( browserLocation ) )
    {
      _browserLocation.changeLocation( browserLocation );
    }
    else if ( isValid( _browserLocation.getLocation() ) )
    {
      _browserLocation.resetBrowserLocation();
    }
    else
    {
      _browserLocation.changeLocation( "" );
    }
  }

  private boolean isValid( @Nonnull final String location )
  {
    return "active".equals( location ) ||
           "completed".equals( location ) ||
           "".equals( location );
  }
}
