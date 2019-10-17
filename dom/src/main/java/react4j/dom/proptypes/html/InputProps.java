package react4j.dom.proptypes.html;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.intellij.lang.annotations.MagicConstant;
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
  @Nonnull
  public final InputProps accept( String s )
  {
    setAccept( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps alt( String s )
  {
    setAlt( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps async( boolean b )
  {
    setAsync( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps autoComplete( @Nonnull @MagicConstant( valuesFromClass = OnOff.class ) final String s )
  {
    setAutoComplete( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps autoFocus( boolean b )
  {
    setAutoFocus( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps checked( boolean b )
  {
    setChecked( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps disabled( boolean b )
  {
    setDisabled( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps form( String s )
  {
    setForm( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps formAction( String s )
  {
    setFormAction( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps formEncType( String s )
  {
    setFormEncType( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps formMethod( String s )
  {
    setFormMethod( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps formNoValidate( boolean b )
  {
    setFormNoValidate( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps formTarget( String s )
  {
    setFormTarget( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps height( String s )
  {
    setHeight( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps list( String s )
  {
    setList( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps max( String s )
  {
    setMax( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps maxLength( int b )
  {
    setMaxLength( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps min( String s )
  {
    setMin( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps multiple( boolean b )
  {
    setMultiple( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps name( String s )
  {
    setName( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps placeHolder( String s )
  {
    setPlaceholder( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps readonly( boolean b )
  {
    setReadOnly( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps required( boolean b )
  {
    setRequired( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps size( int b )
  {
    setSize( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps src( String s )
  {
    setSrc( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps step( String s )
  {
    setStep( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps type( @Nonnull @MagicConstant( valuesFromClass = InputType.class ) final String type )
  {
    setType( type );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps value( String s )
  {
    setValue( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps width( int i )
  {
    setWidth( Integer.toString( i ) );
    return self();
  }

  //React Specific

  @JsOverlay
  @Nonnull
  public final InputProps defaultChecked( boolean b )
  {
    setDefaultChecked( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps defaultValue( String s )
  {
    setDefaultValue( s );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  @Nonnull
  public final InputProps onBlur( @Nullable final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onFocus( @Nullable final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Form Events
  @JsOverlay
  @Nonnull
  public final InputProps onChange( @Nullable final FormEventHandler handler )
  {
    setOnChange( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  @Nonnull
  public final InputProps onKeyDown( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onKeyPress( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onKeyUp( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onClick( @Nullable final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onContextMenu( @Nullable final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDoubleClick( @Nullable final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDrag( @Nullable final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDragEnd( @Nullable final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDragEnter( @Nullable final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDragExit( @Nullable final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDragLeave( @Nullable final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDragOver( @Nullable final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDragStart( @Nullable final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onDrop( @Nullable final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onMouseDown( @Nullable final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onMouseEnter( @Nullable final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onMouseLeave( @Nullable final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onMouseMove( @Nullable final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onMouseOut( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onMouseOver( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onMouseUp( @Nullable final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  @Nonnull
  public final InputProps onTouchCancel( @Nullable final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onTouchEnd( @Nullable final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onTouchMove( @Nullable final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final InputProps onTouchStart( @Nullable final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
