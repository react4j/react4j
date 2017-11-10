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
import react4j.dom.proptypes.html.attributeTypes.OnOff;

/**
 * Props for input elements. Refer to https://developer.mozilla.org/en-US/docs/Web/HTML/Element/textarea
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class TextAreaProps
  extends HtmlGlobalFields<TextAreaProps>
{

  @JsOverlay
  public final TextAreaProps autoComplete( OnOff s )
  {
    setAutoComplete( s.name() );
    return this;
  }

  @JsOverlay
  public final TextAreaProps autoFocus( boolean b )
  {
    setAutoFocus( b );
    return this;
  }

  @JsOverlay
  public final TextAreaProps cols( int b )
  {
    setCols( b );
    return this;
  }

  @JsOverlay
  public final TextAreaProps disabled( boolean b )
  {
    setDisabled( b );
    return this;
  }

  @JsOverlay
  public final TextAreaProps form( String s )
  {
    setForm( s );
    return this;
  }

  @JsOverlay
  public final TextAreaProps maxLength( int l )
  {
    setMaxLength( l );
    return this;
  }

  @JsOverlay
  public final TextAreaProps minLength( int l )
  {
    setMinLength( l );
    return this;
  }

  @JsOverlay
  public final TextAreaProps name( String s )
  {
    setName( s );
    return this;
  }

  @JsOverlay
  public final TextAreaProps placeHolder( String s )
  {
    setPlaceholder( s );
    return this;
  }

  @JsOverlay
  public final TextAreaProps readonly( boolean b )
  {
    setReadOnly( b );
    return this;
  }

  @JsOverlay
  public final TextAreaProps required( boolean b )
  {
    setRequired( b );
    return this;
  }

  @JsOverlay
  public final TextAreaProps rows( int l )
  {
    setRows( l );
    return this;
  }

  @JsOverlay
  public final TextAreaProps value( String s )
  {
    setValue( s );
    return this;
  }

  //React Specific

  @JsOverlay
  public final TextAreaProps defaultValue( String s )
  {
    setDefaultValue( s );
    return this;
  }

  @JsOverlay
  public final TextAreaProps ref( String s )
  {
    ref = s;
    return this;
  }

  @JsOverlay
  public final TextAreaProps ref( ReactRefCallback callback )
  {
    ref = callback;
    return this;
  }

  @JsOverlay
  public final TextAreaProps key( String s )
  {
    key = s;
    return this;
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final TextAreaProps onBlur( FocusEventHandler handler )
  {
    setOnBlur( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onFocus( FocusEventHandler handler )
  {
    setOnFocus( handler );
    return this;
  }

  // Form Events
  @JsOverlay
  public final TextAreaProps onChange( FormEventHandler handler )
  {
    setOnChange( handler );
    return this;
  }

  // Keyboard Events
  @JsOverlay
  public final TextAreaProps onKeyDown( KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onKeyPress( KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onKeyUp( KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onClick( MouseEventHandler handler )
  {
    setOnClick( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onContextMenu( MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDoubleClick( MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDrag( DragEventHandler handler )
  {
    setOnDrag( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDragEnd( DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDragEnter( DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDragExit( DragEventHandler handler )
  {
    setOnDragExit( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDragLeave( DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDragOver( DragEventHandler handler )
  {
    setOnDragOver( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDragStart( DragEventHandler handler )
  {
    setOnDragStart( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onDrop( DragEventHandler handler )
  {
    setOnDrop( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onMouseDown( MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onMouseEnter( MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onMouseLeave( MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onMouseMove( MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onMouseOut( MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onMouseOver( MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onMouseUp( MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return this;
  }

  // Touch Events
  @JsOverlay
  public final TextAreaProps onTouchCancel( TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onTouchEnd( TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onTouchMove( TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return this;
  }

  @JsOverlay
  public final TextAreaProps onTouchStart( TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return this;
  }
}
