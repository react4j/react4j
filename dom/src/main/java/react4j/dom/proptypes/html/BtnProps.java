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
import react4j.dom.proptypes.html.attributeTypes.ButtonType;

/**
 * Props for button elements. Refer to http://www.w3schools.com/tags/tag_button.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class BtnProps
  extends HtmlGlobalFields<BtnProps>
{
  @JsOverlay
  @Nonnull
  public final BtnProps autoFocus( boolean b )
  {
    setAutoFocus( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps disabled( boolean b )
  {
    setDisabled( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps from( String s )
  {
    setForm( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps formAction( String s )
  {
    setFormAction( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps formEncType( String s )
  {
    setFormEncType( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps formMethod( String s )
  {
    setFormMethod( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps formNoValidate( boolean b )
  {
    setFormNoValidate( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps formTarget( String s )
  {
    setFormTarget( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps name( String s )
  {
    setName( s );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps type( @Nonnull @MagicConstant( valuesFromClass = ButtonType.class ) final String t )
  {
    setType( t );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps value( String s )
  {
    setValue( s );
    return self();
  }

  //React Specific

  @JsOverlay
  @Nonnull
  public final BtnProps defaultChecked( boolean b )
  {
    setDefaultChecked( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps defaultValue( String s )
  {
    setDefaultValue( s );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  @Nonnull
  public final BtnProps onBlur( @Nullable final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onFocus( @Nullable final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Form Events
  @JsOverlay
  @Nonnull
  public final BtnProps onSubmit( @Nullable final FormEventHandler handler )
  {
    setOnSubmit( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  @Nonnull
  public final BtnProps onKeyDown( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onKeyPress( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onKeyUp( @Nullable final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onClick( @Nullable final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onContextMenu( @Nullable final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDoubleClick( @Nullable final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDrag( @Nullable final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDragEnd( @Nullable final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDragEnter( @Nullable final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDragExit( @Nullable final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDragLeave( @Nullable final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDragOver( @Nullable final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDragStart( @Nullable final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onDrop( @Nullable final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onMouseDown( @Nullable final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onMouseEnter( @Nullable final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onMouseLeave( @Nullable final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onMouseMove( @Nullable final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onMouseOut( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onMouseOver( @Nullable final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onMouseUp( @Nullable final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  @Nonnull
  public final BtnProps onTouchCancel( @Nullable final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onTouchEnd( @Nullable final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onTouchMove( @Nullable final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final BtnProps onTouchStart( @Nullable final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
