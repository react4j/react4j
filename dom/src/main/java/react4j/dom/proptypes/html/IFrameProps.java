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

/**
 * Props for input elements. Refer to http://www.w3schools.com/tags/tag_iframe.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class IFrameProps
  extends HtmlGlobalFields<IFrameProps>
{

  @JsOverlay
  public final IFrameProps height( int i )
  {
    setHeight( Integer.toString( i ) );
    return self();
  }

  @JsOverlay
  public final IFrameProps name( String s )
  {
    setName( s );
    return self();
  }

  @JsOverlay
  public final IFrameProps src( String s )
  {
    setSrc( s );
    return self();
  }

  @JsOverlay
  public final IFrameProps sandbox( String s )
  {
    setSandbox( s );
    return self();
  }

  @JsOverlay
  public final IFrameProps width( int i )
  {
    setWidth( Integer.toString( i ) );
    return self();
  }

  //Applicable Event Handlers

  //TODO refine
  // Focus Events
  @JsOverlay
  public final IFrameProps onBlur( @Nonnull final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onFocus( @Nonnull final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final IFrameProps onKeyDown( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onKeyPress( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onKeyUp( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onClick( @Nonnull final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onContextMenu( @Nonnull final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDoubleClick( @Nonnull final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDrag( @Nonnull final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDragEnd( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDragEnter( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDragExit( @Nonnull final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDragLeave( @Nonnull final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDragOver( @Nonnull final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDragStart( @Nonnull final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onDrop( @Nonnull final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onMouseDown( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onMouseEnter( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onMouseLeave( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onMouseMove( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onMouseOut( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onMouseOver( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onMouseUp( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final IFrameProps onTouchCancel( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onTouchEnd( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onTouchMove( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final IFrameProps onTouchStart( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
