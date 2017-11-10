package react4j.dom.proptypes.html;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.dom.events.DragEventHandler;
import react4j.dom.events.FocusEventHandler;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.events.ReactEventHandler;
import react4j.dom.events.TouchEventHandler;

/**
 * Props for img elements. Refer to http://www.w3schools.com/tags/tag_img.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class ImgProps
  extends HtmlGlobalFields<ImgProps>
{
  @JsOverlay
  public final ImgProps alt( String s )
  {
    setAlt( s );
    return self();
  }

  @JsOverlay
  public final ImgProps crossOrigin( String s )
  {
    setCrossOrigin( s );
    return self();
  }

  @JsOverlay
  public final ImgProps height( int i )
  {
    setHeight( Integer.toString( i ) );
    return self();
  }

  @JsOverlay
  public final ImgProps src( String s )
  {
    setSrc( s );
    return self();
  }

  @JsOverlay
  public final ImgProps useMap( String s )
  {
    setUseMap( s );
    return self();
  }

  @JsOverlay
  public final ImgProps width( int i )
  {
    setWidth( Integer.toString( i ) );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final ImgProps onBlur( @Nonnull final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onFocus( @Nonnull final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Image Events
  @JsOverlay
  public final ImgProps onLoad( @Nonnull final ReactEventHandler handler )
  {
    setOnLoad( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onError( @Nonnull final ReactEventHandler handler )
  {
    setOnError( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final ImgProps onKeyDown( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onKeyPress( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onKeyUp( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  // Media Events
  @JsOverlay
  public final ImgProps onAbort( @Nonnull final ReactEventHandler handler )
  {
    setOnAbort( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onCanPlay( @Nonnull final ReactEventHandler handler )
  {
    setOnCanPlay( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onCanPlayThrough( @Nonnull final ReactEventHandler handler )
  {
    setOnCanPlayThrough( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDurationChange( @Nonnull final ReactEventHandler handler )
  {
    setOnDurationChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEmptied( @Nonnull final ReactEventHandler handler )
  {
    setOnEmptied( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEncrypted( @Nonnull final ReactEventHandler handler )
  {
    setOnEncrypted( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEnded( @Nonnull final ReactEventHandler handler )
  {
    setOnEnded( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadedData( @Nonnull final ReactEventHandler handler )
  {
    setOnLoadedData( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadedMetadata( @Nonnull final ReactEventHandler handler )
  {
    setOnLoadedMetadata( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadStart( @Nonnull final ReactEventHandler handler )
  {
    setOnLoadStart( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPause( @Nonnull final ReactEventHandler handler )
  {
    setOnPause( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPlay( @Nonnull final ReactEventHandler handler )
  {
    setOnPlay( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPlaying( @Nonnull final ReactEventHandler handler )
  {
    setOnPlaying( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onProgress( @Nonnull final ReactEventHandler handler )
  {
    setOnProgress( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onRateChange( @Nonnull final ReactEventHandler handler )
  {
    setOnRateChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSeeked( @Nonnull final ReactEventHandler handler )
  {
    setOnSeeked( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSeeking( @Nonnull final ReactEventHandler handler )
  {
    setOnSeeking( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onStalled( @Nonnull final ReactEventHandler handler )
  {
    setOnStalled( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSuspend( @Nonnull final ReactEventHandler handler )
  {
    setOnSuspend( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTimeUpdate( @Nonnull final ReactEventHandler handler )
  {
    setOnTimeUpdate( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onVolumeChange( @Nonnull final ReactEventHandler handler )
  {
    setOnVolumeChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onWaiting( @Nonnull final ReactEventHandler handler )
  {
    setOnWaiting( handler );
    return self();
  }

  // MouseEvents
  @JsOverlay
  public final ImgProps onClick( @Nonnull final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onContextMenu( @Nonnull final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDoubleClick( @Nonnull final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDrag( @Nonnull final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragEnd( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragEnter( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragExit( @Nonnull final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragLeave( @Nonnull final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragOver( @Nonnull final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragStart( @Nonnull final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDrop( @Nonnull final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseDown( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseEnter( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseLeave( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseMove( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseOut( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseOver( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseUp( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final ImgProps onTouchCancel( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchEnd( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchMove( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchStart( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
