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
 * Props for a elements. Refer to http://www.w3schools.com/tags/tag_a.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class AnchorProps
  extends HtmlGlobalFields<AnchorProps>
{
  @JsOverlay
  public final AnchorProps download( String s )
  {
    setDownload( s );
    return self();
  }

  @JsOverlay
  public final AnchorProps href( String s )
  {
    setHref( s );
    return self();
  }

  @JsOverlay
  public final AnchorProps hrefLang( String s )
  {
    setHrefLang( s );
    return self();
  }

  @JsOverlay
  public final AnchorProps media( String s )
  {
    setMedia( s );
    return self();
  }

  @JsOverlay
  public final AnchorProps rel( String s )
  {
    setRel( s );
    return self();
  }

  @JsOverlay
  public final AnchorProps referrerPolicy( String s )
  {
    setReferrerPolicy( s );
    return self();
  }

  @JsOverlay
  public final AnchorProps target( String s )
  {
    setTarget( s );
    return self();
  }

  @JsOverlay
  public final AnchorProps type( String t )
  {
    setType( t );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final AnchorProps onBlur( @Nonnull final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onFocus( @Nonnull final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final AnchorProps onKeyDown( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onKeyPress( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onKeyUp( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onClick( @Nonnull final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onContextMenu( @Nonnull final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDoubleClick( @Nonnull final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDrag( @Nonnull final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDragEnd( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDragEnter( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDragExit( @Nonnull final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDragLeave( @Nonnull final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDragOver( @Nonnull final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDragStart( @Nonnull final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onDrop( @Nonnull final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onMouseDown( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onMouseEnter( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onMouseLeave( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onMouseMove( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onMouseOut( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onMouseOver( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onMouseUp( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final AnchorProps onTouchCancel( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onTouchEnd( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onTouchMove( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final AnchorProps onTouchStart( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
