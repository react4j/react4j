package react4j.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import react4j.core.ReactNode;
import react4j.dom.ReactDOM;

/**
 * A GWT widget that renders a React4j element.
 */
public class ReactWidget
  extends Widget
{
  /**
   * The GWT Element representation.
   */
  private final Element _element;
  /**
   * The react widget to render.
   */
  private final ReactNode _reactElement;

  /**
   * Create the widget.
   *
   * @param reactElement the react element to render.
   */
  public ReactWidget( @Nonnull final ReactNode reactElement )
  {
    _element = Document.get().createDivElement();
    _reactElement = Objects.requireNonNull( reactElement );
    setElement( _element );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onAttach()
  {
    super.onAttach();
    ReactDOM.render( _reactElement, Js.uncheckedCast( _element ) );

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDetach()
  {
    ReactDOM.unmountComponentAtNode( Js.uncheckedCast( _element ) );
    super.onDetach();
  }
}
