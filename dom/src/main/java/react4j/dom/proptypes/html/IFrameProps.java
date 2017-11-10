package react4j.dom.proptypes.html;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.core.ReactRefCallback;
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
    return this;
  }

  @JsOverlay
  public final IFrameProps name( String s )
  {
    setName( s );
    return this;
  }

  @JsOverlay
  public final IFrameProps src( String s )
  {
    setSrc( s );
    return this;
  }

  @JsOverlay
  public final IFrameProps sandbox( String s )
  {
    setSandbox( s );
    return this;
  }

  @JsOverlay
  public final IFrameProps width( int i )
  {
    setWidth( Integer.toString( i ) );
    return this;
  }

  //React Specific
  @JsOverlay
  public final IFrameProps ref( String s )
  {
    ref = s;
    return this;
  }

  @JsOverlay
  public final IFrameProps ref( ReactRefCallback callback )
  {
    ref = callback;
    return this;
  }

  @JsOverlay
  public final IFrameProps key( String s )
  {
    key = s;
    return this;
  }

  //Applicable Event Handlers

  //TODO refine
  // Focus Events
  @JsOverlay
  public final IFrameProps onBlur( FocusEventHandler handler )
  {
    setOnBlur( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onFocus( FocusEventHandler handler )
  {
    setOnFocus( handler );
    return this;
  }

  // Keyboard Events
  @JsOverlay
  public final IFrameProps onKeyDown( KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onKeyPress( KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onKeyUp( KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onClick( MouseEventHandler handler )
  {
    setOnClick( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onContextMenu( MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDoubleClick( MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDrag( DragEventHandler handler )
  {
    setOnDrag( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDragEnd( DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDragEnter( DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDragExit( DragEventHandler handler )
  {
    setOnDragExit( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDragLeave( DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDragOver( DragEventHandler handler )
  {
    setOnDragOver( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDragStart( DragEventHandler handler )
  {
    setOnDragStart( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onDrop( DragEventHandler handler )
  {
    setOnDrop( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onMouseDown( MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onMouseEnter( MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onMouseLeave( MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onMouseMove( MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onMouseOut( MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onMouseOver( MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onMouseUp( MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return this;
  }

  // Touch Events
  @JsOverlay
  public final IFrameProps onTouchCancel( TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onTouchEnd( TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onTouchMove( TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return this;
  }

  @JsOverlay
  public final IFrameProps onTouchStart( TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return this;
  }
}
