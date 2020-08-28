package react4j.hfg2;

import javax.annotation.Nonnull;

public final class HTMLElementsGenerator
{
  private HTMLElementsGenerator()
  {
  }

  @Nonnull
  public static ElementCollection create()
  {
    final ElementCollection collection = new ElementCollection();
    collection.element( "a", "HTMLAnchorElement" );
    collection.element( "abbr", "HTMLElement" );
    collection.element( "address", "HTMLElement" );
    collection.element( "area", "HTMLAreaElement", false );
    collection.element( "article", "HTMLElement" );
    collection.element( "aside", "HTMLElement" );
    collection.element( "audio", "HTMLAudioElement" );
    collection.element( "b", "HTMLElement" );
    collection.element( "base", "HTMLBaseElement", false );
    collection.element( "bdi", "HTMLElement" );
    collection.element( "bdo", "HTMLElement" );
    collection.element( "blockquote", "HTMLQuoteElement" );
    collection.element( "body", "HTMLBodyElement" );
    collection.element( "br", "HTMLBRElement", false );
    collection.element( "button", "HTMLButtonElement" );
    collection.element( "canvas", "HTMLCanvasElement" );
    collection.element( "caption", "HTMLTableCaptionElement" );
    collection.element( "cite", "HTMLElement" );
    collection.element( "code", "HTMLElement" );
    collection.element( "col", "HTMLTableColElement" );
    collection.element( "colgroup", "HTMLTableColElement" );
    collection.element( "data", "HTMLDataElement" );
    collection.element( "datalist", "HTMLDataListElement" );
    collection.element( "dd", "HTMLElement" );
    collection.element( "del", "HTMLModElement" );
    collection.element( "details", "HTMLDetailsElement" );
    collection.element( "dfn", "HTMLElement" );
    collection.element( "dialog", "HTMLDialogElement" );
    collection.element( "div", "HTMLDivElement" );
    collection.element( "dl", "HTMLDListElement" );
    collection.element( "dt", "HTMLElement" );
    collection.element( "em", "HTMLElement" );
    collection.element( "embed", "HTMLEmbedElement", false );
    collection.element( "fieldset", "HTMLFieldSetElement" );
    collection.element( "figcaption", "HTMLElement" );
    collection.element( "figure", "HTMLElement" );
    collection.element( "footer", "HTMLElement" );
    collection.element( "form", "HTMLFormElement" );
    collection.element( "h1", "HTMLHeadingElement" );
    collection.element( "h2", "HTMLHeadingElement" );
    collection.element( "h3", "HTMLHeadingElement" );
    collection.element( "h4", "HTMLHeadingElement" );
    collection.element( "h5", "HTMLHeadingElement" );
    collection.element( "h6", "HTMLHeadingElement" );
    collection.element( "head", "HTMLHeadElement" );
    collection.element( "header", "HTMLElement" );
    collection.element( "hgroup", "HTMLElement" );
    collection.element( "hr", "HTMLHRElement" );
    collection.element( "html", "HTMLHtmlElement" );
    collection.element( "i", "HTMLElement" );
    collection.element( "iframe", "HTMLIFrameElement" );
    collection.element( "img", "HTMLImageElement", false );
    collection.element( "input", "HTMLInputElement", false );
    collection.element( "ins", "HTMLModElement" );
    collection.element( "kbd", "HTMLElement" );
    collection.element( "label", "HTMLLabelElement" );
    collection.element( "legend", "HTMLLegendElement" );
    collection.element( "li", "HTMLLIElement" );
    collection.element( "link", "HTMLLinkElement", false );
    collection.element( "main", "HTMLElement" );
    collection.element( "map", "HTMLMapElement" );
    collection.element( "mark", "HTMLElement" );
    collection.element( "menu", "HTMLMenuElement" );
    collection.element( "meta", "HTMLMetaElement", false );
    collection.element( "meter", "HTMLMeterElement" );
    collection.element( "nav", "HTMLElement" );
    collection.element( "noscript", "HTMLElement" );
    collection.element( "object", "HTMLObjectElement" );
    collection.element( "ol", "HTMLOListElement" );
    collection.element( "optgroup", "HTMLOptGroupElement" );
    collection.element( "option", "HTMLOptionElement" );
    collection.element( "output", "HTMLOutputElement" );
    collection.element( "p", "HTMLParagraphElement" );
    collection.element( "param", "HTMLParamElement", false );
    collection.element( "picture", "HTMLPictureElement" );
    collection.element( "pre", "HTMLPreElement" );
    collection.element( "progress", "HTMLProgressElement" );
    collection.element( "q", "HTMLQuoteElement" );
    collection.element( "rb", "HTMLElement" );
    collection.element( "rp", "HTMLElement" );
    collection.element( "rt", "HTMLElement" );
    collection.element( "rtc", "HTMLElement" );
    collection.element( "ruby", "HTMLElement" );
    collection.element( "s", "HTMLElement" );
    collection.element( "samp", "HTMLElement" );
    collection.element( "script", "HTMLScriptElement" );
    collection.element( "section", "HTMLElement" );
    collection.element( "select", "HTMLSelectElement" );
    collection.element( "slot", "HTMLSlotElement" );
    collection.element( "small", "HTMLElement" );
    collection.element( "source", "HTMLSourceElement", false );
    collection.element( "span", "HTMLSpanElement" );
    collection.element( "strong", "HTMLElement" );
    collection.element( "style", "HTMLStyleElement" );
    collection.element( "sub", "HTMLElement" );
    collection.element( "summary", "HTMLElement" );
    collection.element( "sup", "HTMLElement" );
    collection.element( "table", "HTMLTableElement" );
    collection.element( "tbody", "HTMLTableSectionElement" );
    collection.element( "td", "HTMLTableDataCellElement" );
    collection.element( "template", "HTMLTemplateElement" );
    collection.element( "textarea", "HTMLTextAreaElement" );
    collection.element( "tfoot", "HTMLTableSectionElement" );
    collection.element( "th", "HTMLTableHeaderCellElement" );
    collection.element( "thead", "HTMLTableSectionElement" );
    collection.element( "time", "HTMLTimeElement" );
    collection.element( "title", "HTMLTitleElement" );
    collection.element( "tr", "HTMLTableRowElement" );
    collection.element( "track", "HTMLTrackElement", false );
    collection.element( "u", "HTMLElement" );
    collection.element( "ul", "HTMLUListElement" );
    collection.element( "var", "HTMLElement" );
    collection.element( "video", "HTMLVideoElement" );
    collection.element( "wbr", "HTMLElement", false );
    return collection;
  }
}
