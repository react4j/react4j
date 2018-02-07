package react4j.extras;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDocument;
import elemental2.dom.Window;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import org.realityforge.anodoc.Unsupported;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;
import react4j.dom.ReactDOM;

/**
 * A portal that opens another window.
 * This was initially designed to be used so that a single application can be
 * split across multiple windows. This would allow a primary window to manage the data and then operate
 * across multiple windows. It also makes it possible to add
 *
 * <p>TODO: In the future we should pass a control in the context that allows the sub-window to close itself.</p>
 */
@ReactComponent
@Unsupported
public abstract class WindowPortal
  extends Component<BaseState, BaseContext>
{
  @FunctionalInterface
  public interface OnCloseCallback
  {
    /**
     * Invoked when the external window is closed.
     */
    void onClose();
  }

  @Nullable
  private Window _externalWindow;
  @Nullable
  private Element _element;

  @Prop
  protected abstract String windowName();

  @Prop
  protected abstract int left();

  @Prop
  protected abstract int top();

  @Prop
  protected abstract int width();

  @Prop
  protected abstract int height();

  @Prop
  protected abstract OnCloseCallback onClose();

  @Prop
  protected abstract ReactNode[] children();

  @Override
  protected void postConstruct()
  {
    _externalWindow = DomGlobal.window.open( "",
                                             windowName(),
                                             "width=" + width() + ",height=" + height() +
                                             ",left=" + left() + ",top=" + top() );
    _externalWindow.addEventListener( "beforeunload", e -> {
      final OnCloseCallback onClose = onClose();
      if ( null != onClose )
      {
        onClose.onClose();
      }
    } );
    final HTMLDocument document = getWindowDocument();
    _element = document.createElement( "div" );
    while ( document.body.childNodes.length > 0 )
    {
      //Unchecked cast as it is from a different window.
      document.body.removeChild( Js.uncheckedCast( document.body.childNodes.item( 0 ) ) );
    }
    document.body.appendChild( _element );
  }

  @Nonnull
  private HTMLDocument getWindowDocument()
  {
    assert null != _externalWindow;
    /*
     * This needs to be an unchecked cast as the prototype for HTMLDocument is the one derived
     * from external window.
     */
    return Js.uncheckedCast( Js.asPropertyMap( _externalWindow ).get( "document" ) );
  }

  @Override
  protected void componentWillUnmount()
  {
    assert null != _externalWindow;
    assert null != _element;
    getWindowDocument().removeChild( _element );
    _externalWindow.close();
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    assert null != _element;
    final ReactNode children = ReactNode.of( children() );
    return ReactDOM.createPortal( children, _element );
  }
}
