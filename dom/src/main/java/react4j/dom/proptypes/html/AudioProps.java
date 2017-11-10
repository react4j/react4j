package react4j.dom.proptypes.html;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.dom.events.DragEventHandler;
import react4j.dom.events.FocusEventHandler;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.events.TouchEventHandler;
import react4j.dom.proptypes.html.attributeTypes.AudioPreload;

/**
 * Props for audio elements. Refer to http://www.w3schools.com/tags/tag_audio.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class AudioProps
  extends HtmlGlobalFields<AudioProps>
{

  @JsOverlay
  public final AudioProps autoPlay( boolean b )
  {
    setAutoPlay( b );
    return self();
  }

  @JsOverlay
  public final AudioProps controls( boolean b )
  {
    setControls( b );
    return self();
  }

  @JsOverlay
  public final AudioProps loop( boolean b )
  {
    setLoop( b );
    return self();
  }

  @JsOverlay
  public final AudioProps muted( boolean b )
  {
    setMuted( b );
    return self();
  }

  @JsOverlay
  public final AudioProps preLoad( AudioPreload s )
  {
    setPreload( s.name() );
    return self();
  }

  @JsOverlay
  public final AudioProps src( String s )
  {
    setSrc( s );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final AudioProps onBlur( @Nonnull final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onFocus( @Nonnull final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final AudioProps onKeyDown( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onKeyPress( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onKeyUp( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onClick( @Nonnull final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onContextMenu( @Nonnull final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDoubleClick( @Nonnull final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDrag( @Nonnull final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDragEnd( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDragEnter( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDragExit( @Nonnull final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDragLeave( @Nonnull final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDragOver( @Nonnull final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDragStart( @Nonnull final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onDrop( @Nonnull final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onMouseDown( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onMouseEnter( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onMouseLeave( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onMouseMove( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onMouseOut( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onMouseOver( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onMouseUp( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final AudioProps onTouchCancel( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onTouchEnd( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onTouchMove( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final AudioProps onTouchStart( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
