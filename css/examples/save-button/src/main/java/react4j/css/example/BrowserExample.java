package react4j.css.example;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.Nullable;
import react4j.css.Css;
import react4j.dom.DOM;
import react4j.dom.ReactDOM;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.CssProps;
import react4j.dom.proptypes.html.HtmlProps;

public final class BrowserExample {
    private BrowserExample() {}

    @JsMethod
    public static boolean bindingsAreSingletons() {
        var button = BrowserExample_Styled_SaveButton.get();
        var status = BrowserExample_Styled_Status.get();
        return button == BrowserExample_Styled_SaveButton.get() && status == BrowserExample_Styled_Status.get();
    }

    @JsMethod
    public static void render(Object container, @Nullable String success, @Nullable String shared) {
        var button = BrowserExample_Styled_SaveButton.get();
        var status = BrowserExample_Styled_Status.get();
        var custom = new CssProps().customProperty("--success", success);
        ReactDOM.render(
                DOM.div(
                        DOM.button(
                                new BtnProps().id("base").data("shared", shared).className(button.style()), "Save"),
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
                                                .data("shared", shared)
                                                .className(button.style()),
                                        "Nested"),
                                DOM.span(new HtmlProps().id("status").className(status.success()), "Status"))),
                container);
    }

    @Css("""
        color: var(--on-accent);

        &:focus-visible {
          outline: 2px solid var(--focus);
        }

        &:disabled {
          opacity: 0.5;
        }

        &.success {
          background-color: var(--success);
        }

        &.cancel {
          background-color: var(--cancel);
          border: 2px dotted currentcolor;
        }
        """)
    abstract static class SaveButton {
        abstract String style();

        abstract String success();

        abstract String cancel();
    }

    @Css("""
        color: var(--on-accent);

        &.success {
          border: 3px solid var(--success);
        }
        """)
    abstract static class Status {
        abstract String style();

        abstract String success();
    }
}
