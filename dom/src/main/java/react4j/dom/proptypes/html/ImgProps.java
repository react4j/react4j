package react4j.dom.proptypes.html;

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
  public final ImgProps onBlur( FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onFocus( FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Image Events
  @JsOverlay
  public final ImgProps onLoad( ReactEventHandler handler )
  {
    setOnLoad( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onError( ReactEventHandler handler )
  {
    setOnError( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final ImgProps onKeyDown( KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onKeyPress( KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onKeyUp( KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  // Media Events
  @JsOverlay
  public final ImgProps onAbort( ReactEventHandler handler )
  {
    setOnAbort( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onCanPlay( ReactEventHandler handler )
  {
    setOnCanPlay( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onCanPlayThrough( ReactEventHandler handler )
  {
    setOnCanPlayThrough( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDurationChange( ReactEventHandler handler )
  {
    setOnDurationChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEmptied( ReactEventHandler handler )
  {
    setOnEmptied( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEncrypted( ReactEventHandler handler )
  {
    setOnEncrypted( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEnded( ReactEventHandler handler )
  {
    setOnEnded( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadedData( ReactEventHandler handler )
  {
    setOnLoadedData( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadedMetadata( ReactEventHandler handler )
  {
    setOnLoadedMetadata( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadStart( ReactEventHandler handler )
  {
    setOnLoadStart( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPause( ReactEventHandler handler )
  {
    setOnPause( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPlay( ReactEventHandler handler )
  {
    setOnPlay( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPlaying( ReactEventHandler handler )
  {
    setOnPlaying( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onProgress( ReactEventHandler handler )
  {
    setOnProgress( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onRateChange( ReactEventHandler handler )
  {
    setOnRateChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSeeked( ReactEventHandler handler )
  {
    setOnSeeked( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSeeking( ReactEventHandler handler )
  {
    setOnSeeking( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onStalled( ReactEventHandler handler )
  {
    setOnStalled( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSuspend( ReactEventHandler handler )
  {
    setOnSuspend( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTimeUpdate( ReactEventHandler handler )
  {
    setOnTimeUpdate( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onVolumeChange( ReactEventHandler handler )
  {
    setOnVolumeChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onWaiting( ReactEventHandler handler )
  {
    setOnWaiting( handler );
    return self();
  }

  // MouseEvents
  @JsOverlay
  public final ImgProps onClick( MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onContextMenu( MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDoubleClick( MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDrag( DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragEnd( DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragEnter( DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragExit( DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragLeave( DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragOver( DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragStart( DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDrop( DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseDown( MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseEnter( MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseLeave( MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseMove( MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseOut( MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseOver( MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseUp( MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final ImgProps onTouchCancel( TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchEnd( TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchMove( TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchStart( TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
