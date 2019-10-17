package react4j.dom.proptypes.html;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.intellij.lang.annotations.MagicConstant;
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
  @Nonnull
  public final AudioProps autoPlay( boolean b )
  {
    setAutoPlay( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps controls( boolean b )
  {
    setControls( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps loop( boolean b )
  {
    setLoop( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps muted( boolean b )
  {
    setMuted( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps preLoad( @Nonnull @MagicConstant( valuesFromClass = AudioPreload.class ) final String s )
  {
    setPreload( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps src( String s )
  {
    setSrc( s );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  @Nonnull
  public final AudioProps onBlur( @Nullable final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onFocus( @Nullable final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  @Nonnull
  public final AudioProps onKeyDown( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onKeyPress( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onKeyUp( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onClick( @Nullable final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onContextMenu( @Nullable final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDoubleClick( @Nullable final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDrag( @Nullable final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDragEnd( @Nullable final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDragEnter( @Nullable final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDragExit( @Nullable final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDragLeave( @Nullable final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDragOver( @Nullable final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDragStart( @Nullable final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onDrop( @Nullable final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onMouseDown( @Nullable final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onMouseEnter( @Nullable final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onMouseLeave( @Nullable final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onMouseMove( @Nullable final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onMouseOut( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onMouseOver( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onMouseUp( @Nullable final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  @Nonnull
  public final AudioProps onTouchCancel( @Nullable final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onTouchEnd( @Nullable final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onTouchMove( @Nullable final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final AudioProps onTouchStart( @Nullable final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
