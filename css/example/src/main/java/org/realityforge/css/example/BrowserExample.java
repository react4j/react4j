package org.realityforge.css.example;

import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import react4j.dom.DOM;
import react4j.dom.ReactDOM;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.CssProps;
import react4j.dom.proptypes.html.HtmlProps;

public final class BrowserExample {
    private BrowserExample() {}

    @JsMethod
    public static void render(Object container) {
        var button = SaveButton.of();
        var status = Status.of();
        var custom = new CssProps();
        Js.asPropertyMap(custom).set("--success", "#04c");
        ReactDOM.render(
                DOM.div(
                        DOM.button(
                                new BtnProps()
                                        .id("base")
                                        .prop("data-shared", Js.asAny("true"))
                                        .className(button.style()),
                                "Save"),
                        DOM.button(new BtnProps().id("success").className(button.success()), "Saved"),
                        DOM.button(new BtnProps().id("cancel").className(button.cancel()), "Cancel"),
                        DOM.button(
                                new BtnProps()
                                        .id("custom")
                                        .className(button.success())
                                        .style(custom),
                                "Custom"),
                        DOM.button(
                                new BtnProps()
                                        .id("disabled")
                                        .className(button.style())
                                        .disabled(),
                                "Disabled"),
                        DOM.button(
                                new BtnProps().id("conflict").className(button.success(), button.cancel()), "Conflict"),
                        DOM.div(
                                new HtmlProps().id("parent").className(button.cancel()),
                                DOM.button(
                                        new BtnProps()
                                                .id("nested")
                                                .prop("data-shared", Js.asAny("true"))
                                                .className(button.style()),
                                        "Nested"),
                                DOM.span(new HtmlProps().id("status").className(status.success()), "Status"))),
                container);
    }
}
