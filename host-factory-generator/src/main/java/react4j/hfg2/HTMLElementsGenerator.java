package react4j.hfg2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public final class HTMLElementsGenerator
{
  private HTMLElementsGenerator()
  {
  }

  @Nonnull
  public static ElementCollection create()
  {
    // This metadata was built up from scraping https://developer.mozilla.org/en-US/docs/Web/HTML/Element
    // and related pages, stripping deprecated elements and massaging data
    final ElementCollection collection = new ElementCollection();
    collection.element( "a",
                        "HTMLAnchorElement",
                        set( "flow content", "interactive content", "palpable content", "phrasing content" ) );
    collection.element( "abbr", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "address", "HTMLElement", set( "flow content", "palpable content" ) );
    collection.element( "area", "HTMLAreaElement", set( "flow content", "phrasing content" ), false );
    collection.element( "article", "HTMLElement", set( "flow content", "palpable content", "sectioning content" ) );
    collection.element( "aside", "HTMLElement", set( "flow content", "palpable content", "sectioning content" ) );
    collection.element( "audio",
                        "HTMLAudioElement",
                        set( "embedded content",
                             "interactive content",
                             "palpable content",
                             "flow content",
                             "phrasing content" ) );
    collection.element( "b", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "base", "HTMLBaseElement", set( "metadata content" ), false );
    collection.element( "bdi", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "bdo", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "blockquote",
                        "HTMLQuoteElement",
                        set( "flow content", "palpable content", "sectioning root" ) );
    collection.element( "body", "HTMLBodyElement", set( "sectioning root" ) );
    collection.element( "br", "HTMLBRElement", set( "flow content", "phrasing content" ), false );
    collection.element( "button",
                        "HTMLButtonElement",
                        set( "submittable",
                             "form-associated element",
                             "flow content",
                             "interactive content",
                             "labelable",
                             "listed",
                             "palpable content",
                             "phrasing content" ) );
    collection.element( "canvas",
                        "HTMLCanvasElement",
                        set( "embedded content", "flow content", "palpable content", "phrasing content" ) );
    collection.element( "caption", "HTMLTableCaptionElement", set() );
    collection.element( "cite", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "code", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "col", "HTMLTableColElement", set() );
    collection.element( "colgroup", "HTMLTableColElement", set() );
    collection.element( "data", "HTMLDataElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "datalist", "HTMLDataListElement", set( "flow content", "phrasing content" ) );
    collection.element( "dd", "HTMLElement", set() );
    collection.element( "del", "HTMLModElement", set( "flow content", "phrasing content" ) );
    collection.element( "details",
                        "HTMLDetailsElement",
                        set( "flow content", "interactive content", "palpable content", "sectioning root" ) );
    collection.element( "dfn", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "dialog", "HTMLDialogElement", set( "flow content", "sectioning root" ) );
    collection.element( "div", "HTMLDivElement", set( "flow content", "palpable content" ) );
    collection.element( "dl", "HTMLDListElement", set( "flow content", "palpable content" ) );
    collection.element( "dt", "HTMLElement", set() );
    collection.element( "em", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "embed",
                        "HTMLEmbedElement",
                        set( "embedded content",
                             "flow content",
                             "interactive content",
                             "palpable content",
                             "phrasing content" ),
                        false );
    collection.element( "fieldset",
                        "HTMLFieldSetElement",
                        set( "flow content",
                             "form-associated element",
                             "listed",
                             "palpable content",
                             "sectioning root" ) );
    collection.element( "figcaption", "HTMLElement", set() );
    collection.element( "figure", "HTMLElement", set( "flow content", "palpable content", "sectioning root" ) );
    collection.element( "footer", "HTMLElement", set( "flow content", "palpable content" ) );
    collection.element( "form", "HTMLFormElement", set( "flow content", "palpable content" ) );
    collection.element( "h1", "HTMLHeadingElement", set( "flow content", "heading content", "palpable content" ) );
    collection.element( "h2", "HTMLHeadingElement", set( "flow content", "heading content", "palpable content" ) );
    collection.element( "h3", "HTMLHeadingElement", set( "flow content", "heading content", "palpable content" ) );
    collection.element( "h4", "HTMLHeadingElement", set( "flow content", "heading content", "palpable content" ) );
    collection.element( "h5", "HTMLHeadingElement", set( "flow content", "heading content", "palpable content" ) );
    collection.element( "h6", "HTMLHeadingElement", set( "flow content", "heading content", "palpable content" ) );
    collection.element( "head", "HTMLHeadElement", set() );
    collection.element( "header", "HTMLElement", set( "flow content", "palpable content" ) );
    collection.element( "hgroup", "HTMLElement", set( "flow content", "heading content", "palpable content" ) );
    collection.element( "hr", "HTMLHRElement", set( "flow content" ) );
    collection.element( "html", "HTMLHtmlElement", set() );
    collection.element( "i", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "iframe",
                        "HTMLIFrameElement",
                        set( "embedded content",
                             "flow content",
                             "interactive content",
                             "palpable content",
                             "phrasing content" ) );
    collection.element( "img",
                        "HTMLImageElement",
                        set( "embedded content",
                             "flow content",
                             "interactive content category",
                             "palpable content",
                             "phrasing content" ),
                        false );
    collection.element( "input",
                        "HTMLInputElement",
                        set( "flow content",
                             "form-associated element",
                             "listed",
                             "palpable content",
                             "phrasing content",
                             "resettable",
                             "submittable",
                             "labelable element" ),
                        false );
    collection.element( "ins", "HTMLModElement", set( "flow content", "phrasing content" ) );
    collection.element( "kbd", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "label",
                        "HTMLLabelElement",
                        set( "flow content",
                             "form-associated element",
                             "interactive content",
                             "palpable content",
                             "phrasing content" ) );
    collection.element( "legend", "HTMLLegendElement", set() );
    collection.element( "li", "HTMLLIElement", set() );
    collection.element( "link",
                        "HTMLLinkElement",
                        set( "metadata content", "flow content", "phrasing content" ),
                        false );
    collection.element( "main", "HTMLElement", set( "flow content", "palpable content" ) );
    collection.element( "map", "HTMLMapElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "mark", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "menu", "HTMLMenuElement", set( "flow content", "palpable content" ) );
    collection.element( "meta",
                        "HTMLMetaElement",
                        set( "metadata content", "flow content", "phrasing content" ),
                        false );
    collection.element( "meter",
                        "HTMLMeterElement",
                        set( "flow content", "labelable content", "palpable content", "phrasing content" ) );
    collection.element( "nav", "HTMLElement", set( "flow content", "palpable content", "sectioning content" ) );
    collection.element( "noscript", "HTMLElement", set( "flow content", "metadata content", "phrasing content" ) );
    collection.element( "object",
                        "HTMLObjectElement",
                        set( "flow content",
                             "phrasing content",
                             "embedded content",
                             "interactive content",
                             "listed",
                             "palpable content",
                             "submittable form-associated element" ) );
    collection.element( "ol", "HTMLOListElement", set( "flow content", "palpable content" ) );
    collection.element( "optgroup", "HTMLOptGroupElement", set() );
    collection.element( "option", "HTMLOptionElement", set() );
    collection.element( "output",
                        "HTMLOutputElement",
                        set( "flow content",
                             "labelable",
                             "listed",
                             "palpable content",
                             "phrasing content",
                             "resettable form-associated element" ) );
    collection.element( "p", "HTMLParagraphElement", set( "flow content", "palpable content" ) );
    collection.element( "param", "HTMLParamElement", set(), false );
    collection.element( "picture",
                        "HTMLPictureElement",
                        set( "embedded content", "flow content", "phrasing content" ) );
    collection.element( "pre", "HTMLPreElement", set( "flow content", "palpable content" ) );
    collection.element( "progress",
                        "HTMLProgressElement",
                        set( "flow content", "labelable content", "palpable content", "phrasing content" ) );
    collection.element( "q", "HTMLQuoteElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "rb", "HTMLElement", set() );
    collection.element( "rp", "HTMLElement", set() );
    collection.element( "rt", "HTMLElement", set() );
    collection.element( "rtc", "HTMLElement", set() );
    collection.element( "ruby", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "s", "HTMLElement", set( "flow content", "phrasing content" ) );
    collection.element( "samp", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "script", "HTMLScriptElement", set( "flow content", "metadata content", "phrasing content" ) );
    collection.element( "section", "HTMLElement", set( "flow content", "palpable content", "sectioning content" ) );
    collection.element( "select",
                        "HTMLSelectElement",
                        set( "submittable",
                             "form-associated element",
                             "flow content",
                             "interactive content",
                             "labelable",
                             "listed",
                             "phrasing content",
                             "resettable" ) );
    collection.element( "slot", "HTMLSlotElement", set( "flow content", "phrasing content" ) );
    collection.element( "small", "HTMLElement", set( "flow content", "phrasing content" ) );
    collection.element( "source", "HTMLSourceElement", set(), false );
    collection.element( "span",
                        "HTMLSpanElement (before HTML5, the interface was HTMLElement)",
                        set( "flow content", "phrasing content" ) );
    collection.element( "strong", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "style", "HTMLStyleElement", set( "flow content", "metadata content" ) );
    collection.element( "sub", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "summary", "HTMLElement", set() );
    collection.element( "sup", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "table", "HTMLTableElement", set( "flow content" ) );
    collection.element( "tbody", "HTMLTableSectionElement", set() );
    collection.element( "td", "HTMLTableDataCellElement", set( "sectioning root" ) );
    collection.element( "template",
                        "HTMLTemplateElement",
                        set( "flow content", "metadata content", "phrasing content", "script-supporting element" ) );
    collection.element( "textarea",
                        "HTMLTextAreaElement",
                        set( "submittable",
                             "form-associated element",
                             "flow content",
                             "interactive content",
                             "labelable",
                             "listed",
                             "phrasing content",
                             "resettable" ) );
    collection.element( "tfoot", "HTMLTableSectionElement", set() );
    collection.element( "th", "HTMLTableHeaderCellElement", set() );
    collection.element( "thead", "HTMLTableSectionElement", set() );
    collection.element( "time", "HTMLTimeElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "title", "HTMLTitleElement", set( "metadata content" ) );
    collection.element( "tr", "HTMLTableRowElement", set() );
    collection.element( "track", "HTMLTrackElement", set(), false );
    collection.element( "u", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "ul", "HTMLUListElement", set( "flow content", "palpable content" ) );
    collection.element( "var", "HTMLElement", set( "flow content", "palpable content", "phrasing content" ) );
    collection.element( "video",
                        "HTMLVideoElement",
                        set( "embedded content",
                             "interactive content",
                             "palpable content",
                             "flow content",
                             "phrasing content" ) );
    collection.element( "wbr", "HTMLElement", set( "flow content", "phrasing content" ), false );

    return collection;
  }

  @Nonnull
  private static Set<String> set( @Nonnull final String... values )
  {
    return Collections.unmodifiableSet( new HashSet<>( Arrays.asList( values ) ) );
  }
}
