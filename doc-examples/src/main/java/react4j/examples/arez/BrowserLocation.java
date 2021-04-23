package react4j.examples.arez;

import akasha.HashChangeEvent;
import akasha.HashChangeEventListener;
import akasha.Location;
import akasha.WindowGlobal;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Observable;
import arez.annotations.PostConstruct;
import arez.annotations.PreDispose;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * This is a simple abstraction over browser location as a hash.
 * The model exposes the observable values for the location as the application sees it via
 * {@link #getLocation()}, the way the browser sees it via {@link #getBrowserLocation()}.
 * The application code should define an autorun that monitors the location as the browser
 * sees it and update the location as the application sees it via {@link #changeLocation(String)}
 * if the browser location is valid. Otherwise the browser location should be reset to the application
 * location.
 *
 * <p>It should be noted that this class is not a router but a primitive that can be used to
 * implement a router. Observing the application location will allow the application to update
 * the view. Observing the browser location will allow the application to decide whether the
 * route should be updated.</p>
 */
@ArezComponent
public abstract class BrowserLocation
{
  @Nonnull
  private final HashChangeEventListener _listener = this::onHashChangeEvent;

  /**
   * The location according to the application.
   */
  @Nonnull
  private String _location = "";
  /**
   * The location according to the browser.
   */
  @Nonnull
  private String _browserLocation = "";
  /**
   * The location that the application is attempting to update the browser to.
   */
  @Nonnull
  private String _targetLocation = "";
  /**
   * Should we prevent the default action associated with hash change?
   */
  private boolean _preventDefault = true;

  /**
   * Create the model object.
   *
   * @return the BrowserLocation instance.
   */
  public static BrowserLocation create()
  {
    return new Arez_BrowserLocation();
  }

  BrowserLocation()
  {
  }

  @PostConstruct
  void postConstruct()
  {
    WindowGlobal.addHashchangeListener( _listener, false );
    _targetLocation = _browserLocation = _location = getHash();
  }

  @PreDispose
  void preDispose()
  {
    WindowGlobal.removeHashchangeListener(  _listener, false );
  }

  /**
   * Return true if component will prevent default actions when hash.
   *
   * @return true if component will prevent default actions when hash.
   */
  public boolean shouldPreventDefault()
  {
    return _preventDefault;
  }

  /**
   * Set a flag to determine whether events default action will be prevented.
   *
   * @param preventDefault true to prevent default action.
   */
  public void setPreventDefault( final boolean preventDefault )
  {
    _preventDefault = preventDefault;
  }

  /**
   * Change the target location to the specified parameter.
   * This will ultimately result in a side-effect that updates the browsers location.
   * This location parameter should not include "#" as the first character.
   *
   * @param targetLocation the location to change to.
   */
  @Action
  public void changeLocation( @Nonnull final String targetLocation )
  {
    _targetLocation = targetLocation;
    if ( targetLocation.equals( getBrowserLocation() ) )
    {
      setLocation( targetLocation );
    }
    setHash( targetLocation );
    /*
     * setHash does not trigger a "hashchange" event so explicitly call the hook here
     */
    updateBrowserLocation();
  }

  /**
   * Revert the browsers location to the application location.
   */
  @Action
  public void resetBrowserLocation()
  {
    changeLocation( getLocation() );
  }

  /**
   * Return the location as the application sees it.
   * This return value does not include a "#" as the first character.
   *
   * @return the location.
   */
  @Observable
  @Nonnull
  public String getLocation()
  {
    return _location;
  }

  @Observable
  void setLocation( @Nonnull final String location )
  {
    _location = Objects.requireNonNull( location );
  }

  @Observable
  @Nonnull
  public String getBrowserLocation()
  {
    return _browserLocation;
  }

  void setBrowserLocation( @Nonnull final String browserLocation )
  {
    _browserLocation = Objects.requireNonNull( browserLocation );
  }

  @Action
  void updateBrowserLocation()
  {
    final String location = getHash();
    setBrowserLocation( location );
    if ( _targetLocation.equals( location ) )
    {
      setLocation( location );
    }
  }

  private void onHashChangeEvent( @Nonnull final HashChangeEvent e )
  {
    if ( _preventDefault )
    {
      e.preventDefault();
    }
    updateBrowserLocation();
  }

  @Nonnull
  private String getHash()
  {
    return WindowGlobal.location().hash.substring( 1 );
  }

  private void setHash( @Nonnull final String hash )
  {
    final Location location = WindowGlobal.location();
    if ( 0 == hash.length() )
    {
      /*
       * This code is needed to remove the stray #.
       * See https://stackoverflow.com/questions/1397329/how-to-remove-the-hash-from-window-location-url-with-javascript-without-page-r/5298684#5298684
       */
      WindowGlobal.history().pushState( "", WindowGlobal.document().title, location.pathname + location.search );
    }
    else
    {
      location.hash = hash;
    }
  }
}
