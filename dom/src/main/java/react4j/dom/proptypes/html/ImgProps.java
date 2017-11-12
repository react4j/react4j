package react4j.dom.proptypes.html;

import javax.annotation.Nullable;
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
  public final ImgProps onBlur( @Nullable final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onFocus( @Nullable final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Image Events
  @JsOverlay
  public final ImgProps onLoad( @Nullable final ReactEventHandler handler )
  {
    setOnLoad( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onError( @Nullable final ReactEventHandler handler )
  {
    setOnError( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final ImgProps onKeyDown( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onKeyPress( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onKeyUp( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  // Media Events
  @JsOverlay
  public final ImgProps onAbort( @Nullable final ReactEventHandler handler )
  {
    setOnAbort( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onCanPlay( @Nullable final ReactEventHandler handler )
  {
    setOnCanPlay( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onCanPlayThrough( @Nullable final ReactEventHandler handler )
  {
    setOnCanPlayThrough( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDurationChange( @Nullable final ReactEventHandler handler )
  {
    setOnDurationChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEmptied( @Nullable final ReactEventHandler handler )
  {
    setOnEmptied( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEncrypted( @Nullable final ReactEventHandler handler )
  {
    setOnEncrypted( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onEnded( @Nullable final ReactEventHandler handler )
  {
    setOnEnded( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadedData( @Nullable final ReactEventHandler handler )
  {
    setOnLoadedData( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadedMetadata( @Nullable final ReactEventHandler handler )
  {
    setOnLoadedMetadata( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onLoadStart( @Nullable final ReactEventHandler handler )
  {
    setOnLoadStart( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPause( @Nullable final ReactEventHandler handler )
  {
    setOnPause( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPlay( @Nullable final ReactEventHandler handler )
  {
    setOnPlay( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onPlaying( @Nullable final ReactEventHandler handler )
  {
    setOnPlaying( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onProgress( @Nullable final ReactEventHandler handler )
  {
    setOnProgress( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onRateChange( @Nullable final ReactEventHandler handler )
  {
    setOnRateChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSeeked( @Nullable final ReactEventHandler handler )
  {
    setOnSeeked( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSeeking( @Nullable final ReactEventHandler handler )
  {
    setOnSeeking( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onStalled( @Nullable final ReactEventHandler handler )
  {
    setOnStalled( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onSuspend( @Nullable final ReactEventHandler handler )
  {
    setOnSuspend( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTimeUpdate( @Nullable final ReactEventHandler handler )
  {
    setOnTimeUpdate( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onVolumeChange( @Nullable final ReactEventHandler handler )
  {
    setOnVolumeChange( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onWaiting( @Nullable final ReactEventHandler handler )
  {
    setOnWaiting( handler );
    return self();
  }

  // MouseEvents
  @JsOverlay
  public final ImgProps onClick( @Nullable final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onContextMenu( @Nullable final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDoubleClick( @Nullable final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDrag( @Nullable final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragEnd( @Nullable final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragEnter( @Nullable final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragExit( @Nullable final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragLeave( @Nullable final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragOver( @Nullable final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDragStart( @Nullable final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onDrop( @Nullable final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseDown( @Nullable final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseEnter( @Nullable final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseLeave( @Nullable final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseMove( @Nullable final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseOut( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseOver( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onMouseUp( @Nullable final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final ImgProps onTouchCancel( @Nullable final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchEnd( @Nullable final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchMove( @Nullable final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final ImgProps onTouchStart( @Nullable final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
