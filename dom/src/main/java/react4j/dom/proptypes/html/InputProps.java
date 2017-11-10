package react4j.dom.proptypes.html;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.dom.events.DragEventHandler;
import react4j.dom.events.FocusEventHandler;
import react4j.dom.events.FormEventHandler;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.events.TouchEventHandler;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.dom.proptypes.html.attributeTypes.OnOff;

/**
 * Props for input elements. Refer to http://www.w3schools.com/tags/tag_input.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class InputProps
  extends HtmlGlobalFields<InputProps>
{
  @JsOverlay
  public final InputProps accept( String s )
  {
    setAccept( s );
    return self();
  }

  @JsOverlay
  public final InputProps alt( String s )
  {
    setAlt( s );
    return self();
  }

  @JsOverlay
  public final InputProps async( boolean b )
  {
    setAsync( b );
    return self();
  }

  @JsOverlay
  public final InputProps autoComplete( OnOff s )
  {
    setAutoComplete( s.name() );
    return self();
  }

  @JsOverlay
  public final InputProps autoFocus( boolean b )
  {
    setAutoFocus( b );
    return self();
  }

  @JsOverlay
  public final InputProps checked( boolean b )
  {
    setChecked( b );
    return self();
  }

  @JsOverlay
  public final InputProps disabled( boolean b )
  {
    setDisabled( b );
    return self();
  }

  @JsOverlay
  public final InputProps form( String s )
  {
    setForm( s );
    return self();
  }

  @JsOverlay
  public final InputProps formAction( String s )
  {
    setFormAction( s );
    return self();
  }

  @JsOverlay
  public final InputProps formEncType( String s )
  {
    setFormEncType( s );
    return self();
  }

  @JsOverlay
  public final InputProps formMethod( String s )
  {
    setFormMethod( s );
    return self();
  }

  @JsOverlay
  public final InputProps formNoValidate( boolean b )
  {
    setFormNoValidate( b );
    return self();
  }

  @JsOverlay
  public final InputProps formTarget( String s )
  {
    setFormTarget( s );
    return self();
  }

  @JsOverlay
  public final InputProps height( String s )
  {
    setHeight( s );
    return self();
  }

  @JsOverlay
  public final InputProps list( String s )
  {
    setList( s );
    return self();
  }

  @JsOverlay
  public final InputProps max( String s )
  {
    setMax( s );
    return self();
  }

  @JsOverlay
  public final InputProps maxLength( int b )
  {
    setMaxLength( b );
    return self();
  }

  @JsOverlay
  public final InputProps min( String s )
  {
    setMin( s );
    return self();
  }

  @JsOverlay
  public final InputProps multiple( boolean b )
  {
    setMultiple( b );
    return self();
  }

  @JsOverlay
  public final InputProps name( String s )
  {
    setName( s );
    return self();
  }

  @JsOverlay
  public final InputProps placeHolder( String s )
  {
    setPlaceholder( s );
    return self();
  }

  @JsOverlay
  public final InputProps readonly( boolean b )
  {
    setReadOnly( b );
    return self();
  }

  @JsOverlay
  public final InputProps required( boolean b )
  {
    setRequired( b );
    return self();
  }

  @JsOverlay
  public final InputProps size( int b )
  {
    setSize( b );
    return self();
  }

  @JsOverlay
  public final InputProps src( String s )
  {
    setSrc( s );
    return self();
  }

  @JsOverlay
  public final InputProps step( String s )
  {
    setStep( s );
    return self();
  }

  @JsOverlay
  public final InputProps type( InputType t )
  {
    setType( t.name() );
    return self();
  }

  @JsOverlay
  public final InputProps value( String s )
  {
    setValue( s );
    return self();
  }

  @JsOverlay
  public final InputProps width( int i )
  {
    setWidth( Integer.toString( i ) );
    return self();
  }

  //React Specific

  @JsOverlay
  public final InputProps defaultChecked( boolean b )
  {
    setDefaultChecked( b );
    return self();
  }

  ;

  @JsOverlay
  public final InputProps defaultValue( String s )
  {
    setDefaultValue( s );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final InputProps onBlur( @Nonnull final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onFocus( @Nonnull final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Form Events
  @JsOverlay
  public final InputProps onChange( @Nonnull final FormEventHandler handler )
  {
    setOnChange( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final InputProps onKeyDown( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onKeyPress( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onKeyUp( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onClick( @Nonnull final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onContextMenu( @Nonnull final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDoubleClick( @Nonnull final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDrag( @Nonnull final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDragEnd( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDragEnter( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDragExit( @Nonnull final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDragLeave( @Nonnull final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDragOver( @Nonnull final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDragStart( @Nonnull final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onDrop( @Nonnull final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onMouseDown( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onMouseEnter( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onMouseLeave( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onMouseMove( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onMouseOut( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onMouseOver( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onMouseUp( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final InputProps onTouchCancel( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onTouchEnd( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onTouchMove( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final InputProps onTouchStart( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
