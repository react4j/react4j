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

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class HtmlProps
  extends HtmlGlobalFields<HtmlProps>
{
  //Applicable Event Handlers

  // Focus Events
  @JsOverlay
  public final HtmlProps onBlur( @Nonnull final FocusEventHandler handler )
  {
    setOnBlur( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onFocus( @Nonnull final FocusEventHandler handler )
  {
    setOnFocus( handler );
    return self();
  }

  // Keyboard Events
  @JsOverlay
  public final HtmlProps onKeyDown( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyDown( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onKeyPress( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyPress( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onKeyUp( @Nonnull final KeyboardEventHandler handler )
  {
    setOnKeyUp( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onClick( @Nonnull final MouseEventHandler handler )
  {
    setOnClick( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onContextMenu( @Nonnull final MouseEventHandler handler )
  {
    setOnContextMenu( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDoubleClick( @Nonnull final MouseEventHandler handler )
  {
    setOnDoubleClick( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDrag( @Nonnull final DragEventHandler handler )
  {
    setOnDrag( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDragEnd( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnd( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDragEnter( @Nonnull final DragEventHandler handler )
  {
    setOnDragEnter( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDragExit( @Nonnull final DragEventHandler handler )
  {
    setOnDragExit( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDragLeave( @Nonnull final DragEventHandler handler )
  {
    setOnDragLeave( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDragOver( @Nonnull final DragEventHandler handler )
  {
    setOnDragOver( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDragStart( @Nonnull final DragEventHandler handler )
  {
    setOnDragStart( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onDrop( @Nonnull final DragEventHandler handler )
  {
    setOnDrop( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onMouseDown( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseDown( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onMouseEnter( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseEnter( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onMouseLeave( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseLeave( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onMouseMove( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseMove( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onMouseOut( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOut( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onMouseOver( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseOver( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onMouseUp( @Nonnull final MouseEventHandler handler )
  {
    setOnMouseUp( handler );
    return self();
  }

  // Touch Events
  @JsOverlay
  public final HtmlProps onTouchCancel( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchCancel( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onTouchEnd( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchEnd( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onTouchMove( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchMove( handler );
    return self();
  }

  @JsOverlay
  public final HtmlProps onTouchStart( @Nonnull final TouchEventHandler handler )
  {
    setOnTouchStart( handler );
    return self();
  }
}
