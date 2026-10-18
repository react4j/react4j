package react4j.css.example;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.Nullable;
import react4j.css.Css;
import react4j.css.CssCustomProperty;
import react4j.css.CssImport;
import react4j.css.GlobalCss;
import react4j.css.Keyframes;
import react4j.dom.DOM;
import react4j.dom.ReactDOM;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.CssProps;
import react4j.dom.proptypes.html.HtmlProps;

public final class BrowserExample {
    private BrowserExample() {}

    @JsMethod
    public static boolean bindingsAreSingletons() {
        var button = SaveButton.get();
        var status = Status.get();
        return button == SaveButton.get() && status == Status.get();
    }

    @JsMethod
    public static String successProperty() {
        return SaveButton.get().successProperty();
    }

    @JsMethod
    public static void render(Object container, @Nullable String success, @Nullable String shared) {
        var button = SaveButton.get();
        var status = Status.get();
        var custom = new CssProps().customProperty(button.successProperty(), success);
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
                                DOM.span(new HtmlProps().id("status").className(status.success()), "Status")),
                        DOM.button(new BtnProps().id("busy-button").className(button.busy()), "Saving"),
                        DOM.span(new HtmlProps().id("busy-status").className(status.busy()), "Saving status"),
                        DOM.button(new BtnProps().id("peer-button").className(button.style()), "Focus to show status"),
                        DOM.span(
                                new HtmlProps()
                                        .id("peer-label")
                                        .data("peer", "true")
                                        .className(status.style()),
                                "Ready")),
                container);
    }

    @CssImport(Defaults.class)
    @CssImport(value = Defaults.class, property = "on-accent")
    @CssImport(value = Defaults.class, property = "success")
    @CssImport(name = "pulse", value = Pulse.class)
    @CssImport(name = "status", value = Status.class)
    @CssImport(value = Defaults.class, property = "focus")
    @CssImport(value = Defaults.class, property = "cancel")
    @Css("""
        color: var(--on-accent);

        &:focus-visible {
          outline: 2px solid var(--focus);
        }

        &:disabled {
          opacity: 0.5;
        }

        &:focus-visible + :css(status)[data-peer] {
          color: var(--success);
        }

        &.success {
          background-color: var(--success);
        }

        &.cancel {
          background-color: var(--cancel);
          border: 2px dotted currentcolor;
        }

        &.busy {
          animation: pulse 0.8s ease-in-out infinite alternate;
        }
        """)
    abstract static class SaveButton {
        static SaveButton get() {
            return BrowserExample_Styled_SaveButton.get();
        }

        abstract String style();

        abstract String success();

        abstract String cancel();

        abstract String busy();

        @CssCustomProperty(name = "success")
        abstract String successProperty();
    }

    @CssImport(Defaults.class)
    @CssImport(value = Defaults.class, property = "on-accent")
    @CssImport(value = Defaults.class, property = "success")
    @CssImport(name = "pulse", value = Pulse.class)
    @Css("""
        color: var(--on-accent);

        &.success {
          border: 3px solid var(--success);
        }

        &.busy {
          animation-name: pulse;
          animation-duration: 0.8s;
          animation-timing-function: ease-in-out;
          animation-iteration-count: infinite;
          animation-direction: alternate;
        }
        """)
    abstract static class Status {
        static Status get() {
            return BrowserExample_Styled_Status.get();
        }

        abstract String style();

        abstract String success();

        abstract String busy();
    }

    @Keyframes("""
        from { opacity: 0.4; }
        to { opacity: 1; }
        """)
    abstract static class Pulse {}

    @Keyframes("""
        to { transform: rotate(360deg); }
        """)
    abstract static class UnusedAnimation {}

    @GlobalCss("""
        :root {
          --on-accent: #fff;
          --focus: #06f;
          --success: #080;
          --cancel: #a00;
        }
        #base { border-top-width: 2px; }
        """)
    static final class Defaults {}

    @CssImport(Defaults.class)
    @GlobalCss("""
        #base { border-top-width: 3px; }
        """)
    static final class Theme {}

    @CssImport(Defaults.class)
    @GlobalCss("""
        #base { border-top-width: 4px; }
        """)
    static final class AlternateTheme {}

    @GlobalCss("""
        #base { --excluded: unwanted; }
        """)
    static final class UnusedGlobals {}
}
