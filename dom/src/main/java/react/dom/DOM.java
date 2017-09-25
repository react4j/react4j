package react.dom;

import java.util.List;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import react.core.ReactElement;
import react.core.util.JsUtil;
import react.dom.proptypes.html.AnchorProps;
import react.dom.proptypes.html.AudioProps;
import react.dom.proptypes.html.BtnProps;
import react.dom.proptypes.html.ColProps;
import react.dom.proptypes.html.FormProps;
import react.dom.proptypes.html.HtmlProps;
import react.dom.proptypes.html.IFrameProps;
import react.dom.proptypes.html.ImgProps;
import react.dom.proptypes.html.InputProps;
import react.dom.proptypes.html.LabelProps;
import react.dom.proptypes.html.OptGroupProps;
import react.dom.proptypes.html.OptionProps;
import react.dom.proptypes.html.SelectProps;
import react.dom.proptypes.html.SourceProps;
import react.dom.proptypes.html.TdProps;
import react.dom.proptypes.html.TextAreaProps;
import react.dom.proptypes.html.ThProps;

/**
 * React.DOM provides convenience wrappers around React.createElement for DOM components.
 * For example React.DOM.div(null, 'Hello World!')
 */
@JsType( isNative = true, namespace = "React" )
public class DOM
{
  /**
   * This is a static class.
   */
  private DOM()
  {
  }

  /**
   * <p>Create and return a new ReactElement of the given type.
   *
   * @param type  a HTML tag name (eg. 'div', 'span', etc)
   * @param props the props to pass to the element
   * @return a {@link DOMElement}
   */
  @JsMethod( namespace = "React" )
  public static native <P extends HtmlProps> DOMElement<P> createElement( String type, P props );

  @JsMethod( namespace = "React" )
  public static native <P extends HtmlProps> DOMElement<P> createElement( String type, P props, String value );

  @JsMethod( namespace = "React" )
  public static native <P extends HtmlProps> DOMElement<P> createElement( String type,
                                                                          P props,
                                                                          ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> a( AnchorProps props, String value );

  public static native DOMElement<HtmlProps> a( AnchorProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> article( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> audio( AudioProps props, ReactElement<?, ?>... child );

  public static native DOMElement<BtnProps> br( HtmlProps props );

  public static native DOMElement<BtnProps> br();

  public static native DOMElement<BtnProps> button( BtnProps props );

  public static native DOMElement<BtnProps> button( BtnProps props, String value );

  public static native DOMElement<BtnProps> canvas( HtmlProps props );

  public static native DOMElement<HtmlProps> caption( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> caption( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> col( ColProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> colgroup( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> div( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> div( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> footer( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> form( FormProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> header( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> h1( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> h2( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> h3( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> h4( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> h5( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> h6( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> iframe( IFrameProps props );

  public static native DOMElement<HtmlProps> img( ImgProps props );

  public static native DOMElement<HtmlProps> input( InputProps props );

  public static native DOMElement<HtmlProps> label( LabelProps props, String value );

  public static native DOMElement<HtmlProps> li( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> li( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> ol( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> option( OptionProps props, String value );

  public static native DOMElement<HtmlProps> optgroup( OptGroupProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> p( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> p( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> span( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> span( HtmlProps props, ReactElement<?, ?> child1 );

  public static native DOMElement<HtmlProps> span( HtmlProps props, ReactElement<?, ?> child1, String value );

  public static native DOMElement<HtmlProps> select( SelectProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> section( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> strong( HtmlProps props, String value );

  public static native DOMElement<HtmlProps> source( SourceProps props );

  public static native DOMElement<HtmlProps> table( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> textarea( TextAreaProps props );

  public static native DOMElement<HtmlProps> td( TdProps props, String value );

  public static native DOMElement<HtmlProps> td( TdProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> th( ThProps props, String value );

  public static native DOMElement<HtmlProps> th( ThProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> tr( HtmlProps props, ReactElement<?, ?>... child );

  public static native DOMElement<HtmlProps> ul( HtmlProps props, ReactElement<?, ?>... child );

  @JsOverlay
  public static DOMElement<HtmlProps> ul( HtmlProps props, List<ReactElement<?, ?>> children )
  {
    return ul( props, Js.<ReactElement<?, ?>[]>uncheckedCast( JsUtil.asJsArray( children ) ) );
  }
}
