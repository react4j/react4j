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
import react4j.dom.proptypes.html.attributeTypes.ButtonType;

/**
 * Props for button elements. Refer to http://www.w3schools.com/tags/tag_button.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class BtnProps
  extends HtmlGlobalFields<BtnProps>
{

  @JsOverlay
  public final BtnProps autoFocus( boolean b )
  {
    setAutoFocus( b );
    return self();
  }

  @JsOverlay
  public final BtnProps disabled( boolean b )
  {
    setDisabled( b );
    return self();
  }

  @JsOverlay
  public final BtnProps from( String s )
  {
    setForm( s );
    return self();
  }

  @JsOverlay
  public final BtnProps formAction( String s )
  {
    setFormAction( s );
    return self();
  }

  @JsOverlay
  public final BtnProps formEncType( String s )
  {
    setFormEncType( s );
    return self();
  }

  @JsOverlay
  public final BtnProps formMethod( String s )
  {
    setFormMethod( s );
    return self();
  }

  @JsOverlay
  public final BtnProps formNoValidate( boolean b )
  {
    setFormNoValidate( b );
    return self();
  }

  @JsOverlay
  public final BtnProps formTarget( String s )
  {
    setFormTarget( s );
    return self();
  }

  @JsOverlay
  public final BtnProps name( String s )
  {
    setName( s );
    return self();
  }

  @JsOverlay
  public final BtnProps type( ButtonType t )
  {
    setType( t.name() );
    return self();
  }

  @JsOverlay
  public final BtnProps value( String s )
  {
    setValue( s );
    return self();
  }

  //React Specific

  @JsOverlay
  public final BtnProps defaultChecked( boolean b )
  {
    setDefaultChecked( b );
    return self();
  }

  @JsOverlay
  public final BtnProps defaultValue( String s )
  {
    setDefaultValue( s );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final BtnProps onBlur( FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onFocus( FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Form Events
  @JsOverlay
  public final BtnProps onSubmit( FormEventHandler handler )
  {
    setOnSubmit( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final BtnProps onKeyDown( KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onKeyPress( KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onKeyUp( KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onClick( MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onContextMenu( MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDoubleClick( MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDrag( DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDragEnd( DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDragEnter( DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDragExit( DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDragLeave( DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDragOver( DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDragStart( DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onDrop( DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onMouseDown( MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onMouseEnter( MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onMouseLeave( MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onMouseMove( MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onMouseOut( MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onMouseOver( MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onMouseUp( MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final BtnProps onTouchCancel( TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onTouchEnd( TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onTouchMove( TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final BtnProps onTouchStart( TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
