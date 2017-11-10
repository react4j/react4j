package react4j.dom.proptypes.html;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.dom.events.DragEventHandler;
import react4j.dom.events.FocusEventHandler;
import react4j.dom.events.FormEventHandler;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.events.TouchEventHandler;

/**
 * Props for select elements. Refer to http://www.w3schools.com/tags/tag_select.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class SelectProps
  extends HtmlGlobalFields<SelectProps>
{

  @JsOverlay
  public final SelectProps autoFocus( boolean b )
  {
    setAutoFocus( b );
    return self();
  }

  @JsOverlay
  public final SelectProps disabled( boolean b )
  {
    setDisabled( b );
    return self();
  }

  @JsOverlay
  public final SelectProps form( String s )
  {
    setForm( s );
    return self();
  }

  @JsOverlay
  public final SelectProps multiple( boolean b )
  {
    setMultiple( b );
    return self();
  }

  @JsOverlay
  public final SelectProps name( String s )
  {
    setName( s );
    return self();
  }

  @JsOverlay
  public final SelectProps required( boolean b )
  {
    setRequired( b );
    return self();
  }

  @JsOverlay
  public final SelectProps size( int b )
  {
    setSize( b );
    return self();
  }

  @JsOverlay
  public final SelectProps value( String s )
  {
    setValue( s );
    return self();
  }

  @JsOverlay
  public final SelectProps defaultValue( String s )
  {
    setDefaultValue( s );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final SelectProps onBlur( FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onFocus( FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Form Events
  @JsOverlay
  public final SelectProps onChange( FormEventHandler handler )
  {
    setOnChange( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final SelectProps onKeyDown( KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onKeyPress( KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onKeyUp( KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onClick( MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onContextMenu( MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDoubleClick( MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDrag( DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDragEnd( DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDragEnter( DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDragExit( DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDragLeave( DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDragOver( DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDragStart( DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onDrop( DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onMouseDown( MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onMouseEnter( MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onMouseLeave( MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onMouseMove( MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onMouseOut( MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onMouseOver( MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onMouseUp( MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final SelectProps onTouchCancel( TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onTouchEnd( TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onTouchMove( TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final SelectProps onTouchStart( TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
