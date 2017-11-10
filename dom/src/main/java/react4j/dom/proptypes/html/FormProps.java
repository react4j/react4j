package react4j.dom.proptypes.html;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.core.ReactRefCallback;
import react4j.dom.events.DragEventHandler;
import react4j.dom.events.FocusEventHandler;
import react4j.dom.events.FormEventHandler;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.events.TouchEventHandler;
import react4j.dom.proptypes.html.attributeTypes.FormMethod;
import react4j.dom.proptypes.html.attributeTypes.OnOff;
import react4j.dom.proptypes.html.attributeTypes.Target;

/**
 * Props for form elements. Refer to http://www.w3schools.com/tags/tag_form.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class FormProps
  extends HtmlGlobalFields<FormProps>
{

  @JsOverlay
  public final FormProps acceptCharset( String s )
  {
    setAcceptCharset( s );
    return self();
  }

  @JsOverlay
  public final FormProps action( String s )
  {
    setAction( s );
    return self();
  }

  @JsOverlay
  public final FormProps autoComplete( OnOff s )
  {
    setAutoComplete( s.name() );
    return self();
  }

  @JsOverlay
  public final FormProps formEncType( String s )
  {
    setFormEncType( s );
    return self();
  }

  @JsOverlay
  public final FormProps formMethod( FormMethod s )
  {
    setFormMethod( s.name() );
    return self();
  }

  @JsOverlay
  public final FormProps name( String s )
  {
    setName( s );
    return self();
  }

  @JsOverlay
  public final FormProps formNoValidate( boolean b )
  {
    setFormNoValidate( b );
    return self();
  }

  @JsOverlay
  public final FormProps formTarget( Target t )
  {
    setFormTarget( t.name() );
    return self();
  }

  @JsOverlay
  public final FormProps formTarget( String s )
  {
    setFormTarget( s );
    return self();
  }

  //React Specific
  @JsOverlay
  public final FormProps ref( String s )
  {
    ref = s;
    return self();
  }

  @JsOverlay
  public final FormProps ref( ReactRefCallback callback )
  {
    ref = callback;
    return self();
  }

  @JsOverlay
  public final FormProps key( String s )
  {
    key = s;
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final FormProps onBlur( FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onFocus( FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Form Events
  @JsOverlay
  public final FormProps onChange( FormEventHandler handler )
  {
    setOnChange( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onInput( FormEventHandler handler )
  {
    setOnInput( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onSubmit( FormEventHandler handler )
  {
    setOnSubmit( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final FormProps onKeyDown( KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onKeyPress( KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onKeyUp( KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onClick( MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onContextMenu( MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDoubleClick( MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDrag( DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDragEnd( DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDragEnter( DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDragExit( DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDragLeave( DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDragOver( DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDragStart( DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onDrop( DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onMouseDown( MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onMouseEnter( MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onMouseLeave( MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onMouseMove( MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onMouseOut( MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onMouseOver( MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onMouseUp( MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final FormProps onTouchCancel( TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onTouchEnd( TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onTouchMove( TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final FormProps onTouchStart( TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
