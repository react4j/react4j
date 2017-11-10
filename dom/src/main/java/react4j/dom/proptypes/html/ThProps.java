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
import react4j.dom.proptypes.html.attributeTypes.HeaderScope;

/**
 * Props for td elements. Refer to http://www.w3schools.com/tags/tag_td.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class ThProps
  extends HtmlGlobalFields<ThProps>
{

  @JsOverlay
  public final ThProps colSpan( int i )
  {
    setColSpan( i );
    return self();
  }

  @JsOverlay
  public final ThProps headers( String headerId )
  {
    setHeaders( headerId );
    return self();
  }

  @JsOverlay
  public final ThProps rowSpan( int i )
  {
    setRowSpan( i );
    return self();
  }

  @JsOverlay
  public final ThProps scope( HeaderScope s )
  {
    setScope( s.name() );
    return self();
  }

  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final ThProps onBlur( @Nonnull final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onFocus( @Nonnull final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final ThProps onKeyDown( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onKeyPress( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onKeyUp( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onClick( @Nonnull final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onContextMenu( @Nonnull final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDoubleClick( @Nonnull final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDrag( @Nonnull final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDragEnd( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDragEnter( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDragExit( @Nonnull final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDragLeave( @Nonnull final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDragOver( @Nonnull final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDragStart( @Nonnull final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onDrop( @Nonnull final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onMouseDown( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onMouseEnter( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onMouseLeave( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onMouseMove( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onMouseOut( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onMouseOver( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onMouseUp( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final ThProps onTouchCancel( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onTouchEnd( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onTouchMove( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final ThProps onTouchStart( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
