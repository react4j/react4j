package react4j.css.example;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.Nullable;
import react4j.css.Css;
import react4j.css.CssAnimation;
import react4j.css.CssCustomProperty;
import react4j.css.CssId;
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
                                "Ready"),
                        DOM.button(
                                new BtnProps()
                                        .id(button.controlId())
                                        .data("symbol", "control")
                                        .className(button.style()),
                                "Checked ID"),
                        DOM.div(new HtmlProps()
                                .dangerouslySetInnerHTML(
                                        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"0\" height=\"0\"><filter"
                                                + " id=\""
                                                + button.filterId()
                                                + "\"><feGaussianBlur stdDeviation=\"0.25\"/></filter></svg>")),
                        DOM.span(
                                new HtmlProps()
                                        .id("direct-animation")
                                        .className(status.style())
                                        .style(new CssProps()
                                                .animationName(DirectPulse.get().name())
                                                .animationDuration("1s")),
                                "Direct animation"),
                        DOM.span(
                                new HtmlProps()
                                        .id("local-animation")
                                        .className(button.style())
                                        .style(new CssProps()
                                                .animationName(button.localAnimation())
                                                .animationDuration("1s")),
                                "Local animation"),
                        DOM.span(
                                new HtmlProps()
                                        .id("accessor-animation")
                                        .className(status.style())
                                        .style(new CssProps()
                                                .animationName(status.externalAnimation())
                                                .animationDuration("1s")),
                                "Imported animation"),
                        DOM.span(
                                new HtmlProps()
                                        .id(DirectGlobals.get().id())
                                        .data("symbol", "global")
                                        .style(new CssProps()
                                                .customProperty(
                                                        DirectGlobals.get().spacing(), "9px")),
                                "Direct global")),
                container);
    }

    @CssImport(Defaults.class)
    @CssImport(value = Defaults.class, property = "on-accent")
    @CssImport(value = Defaults.class, property = "success")
    @CssImport(name = "pulse", value = Pulse.class)
    @CssImport(name = "status", value = Status.class)
    @CssImport(value = Defaults.class, property = "focus")
    @CssImport(value = Defaults.class, property = "cancel")
    @CssImport(value = Defaults.class, property = "theme-color")
    @Css("""
        @keyframes runtime-pulse { from { opacity: 1; } to { opacity: .8; } }
        &#control {
          border-bottom: 5px solid;
          filter: url(#filter);
          background-color: var(--theme-color);
        }
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

        @CssId(name = "control")
        abstract String controlId();

        @CssId(name = "filter")
        abstract String filterId();

        @CssAnimation(name = "runtime-pulse")
        abstract String localAnimation();
    }

    @CssImport(name = "accessor-pulse", value = AccessorPulse.class)
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

        @CssAnimation(name = "accessor-pulse")
        abstract String externalAnimation();
    }

    @Keyframes("""
        from { opacity: 0.4; }
        to { opacity: 1; }
        """)
    abstract static class Pulse {}

    @Keyframes("""
        from { opacity: 1; }
        to { opacity: .8; }
        """)
    abstract static class DirectPulse {
        static DirectPulse get() {
            return BrowserExample_Styled_DirectPulse.get();
        }

        @CssAnimation
        abstract String name();
    }

    @Keyframes("""
        from { transform: translateX(0); }
        to { transform: translateX(1px); }
        """)
    abstract static class AccessorPulse {}

    @GlobalCss("""
        #direct-global { --spacing: 7px; letter-spacing: var(--spacing); }
        """)
    abstract static class DirectGlobals {
        static DirectGlobals get() {
            return BrowserExample_Styled_DirectGlobals.get();
        }

        @CssId(name = "direct-global")
        abstract String id();

        @CssCustomProperty
        abstract String spacing();
    }

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
          --theme-color: #080;
        }
        #base { border-top-width: 2px; }
        """)
    static final class Defaults {}

    @CssImport(Defaults.class)
    @CssImport(value = Defaults.class, property = "theme-color")
    @GlobalCss("""
        :root { --theme-color: #909; }
        #base { border-top-width: 3px; }
        """)
    static final class Theme {}

    @CssImport(Defaults.class)
    @CssImport(value = Defaults.class, property = "theme-color")
    @GlobalCss("""
        :root { --theme-color: #099; }
        #base { border-top-width: 4px; }
        """)
    static final class AlternateTheme {}

    @GlobalCss("""
        #base { --excluded: unwanted; }
        """)
    static final class UnusedGlobals {}
}
