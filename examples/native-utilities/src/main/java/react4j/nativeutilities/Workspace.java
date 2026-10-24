package react4j.nativeutilities;

import jsinterop.base.Js;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.css.CssImport;
import react4j.css.GlobalCss;
import react4j.css.UtilityCss;
import react4j.css.UtilityStyles;
import react4j.dom.DOM;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;

@View(type = View.Type.STATEFUL)
abstract class Workspace {
    private boolean _narrow;
    private boolean _saved;

    @ScheduleRender
    abstract void scheduleRender();

    private void toggleWidth() {
        _narrow = !_narrow;
        scheduleRender();
    }

    private void toggleSaved() {
        _saved = !_saved;
        scheduleRender();
    }

    @Render
    ReactNode render() {
        var styles = Styles.get();
        var buttons = Buttons.get();
        return DOM.section(
                new HtmlProps().className(styles.style()).prop("role", Js.asAny("main")),
                DOM.h1("Native utility rules"),
                DOM.p("Resize the container, change a button state, and open details anchored to their own trigger."),
                DOM.div(
                        new HtmlProps().className(styles.controls()),
                        DOM.button(
                                new BtnProps()
                                        .id("resize")
                                        .className(buttons.style(), buttons.quiet())
                                        .prop("aria-pressed", Js.asAny(_narrow))
                                        .onClick(e -> toggleWidth()),
                                _narrow ? "Use wide container" : "Use narrow container"),
                        DOM.button(
                                new BtnProps()
                                        .id("save")
                                        .className(buttons.style(), _saved ? buttons.saved() : buttons.ready())
                                        .prop("aria-pressed", Js.asAny(_saved))
                                        .onClick(e -> toggleSaved()),
                                _saved ? "Saved" : "Save changes")),
                DOM.div(
                        new HtmlProps().id("notes").className(_narrow ? styles.narrow() : styles.frame()),
                        note(
                                "rules",
                                "A small, explicit vocabulary",
                                "Spacing and colors come from this example's rule library.",
                                "p-5 means five spacing steps. bg-accent selects a named color. These names are"
                                        + " declared in utilities.css; the language supplies no built-in utility set."),
                        note(
                                "scope",
                                "One anchor scope per instance",
                                "Both Details buttons use the same checked utility names.",
                                "Each card scopes its trigger and panel. The native popover enters the top layer,"
                                        + " while CSS supplies placement, flips and size limits.")),
                DOM.p("Wide containers put each title and its Details button on one line. Narrow containers stack"
                        + " them."));
    }

    private static ReactNode note(String id, String title, String summary, String detail) {
        var styles = Styles.get();
        var buttons = Buttons.get();
        String panel = id + "-panel";
        return DOM.article(
                new HtmlProps().id(id + "-card").className(styles.card()),
                DOM.div(DOM.h2(title), DOM.p(new HtmlProps().className(styles.muted()), summary)),
                DOM.button(
                        new BtnProps()
                                .id(id + "-trigger")
                                .className(buttons.style(), buttons.quiet(), buttons.trigger())
                                .prop("aria-label", Js.asAny("Details: " + title))
                                .prop("popovertarget", Js.asAny(panel)),
                        "Details"),
                DOM.div(
                        new HtmlProps()
                                .id(panel)
                                .className(styles.panel())
                                .prop("popover", Js.asAny("auto"))
                                .prop("role", Js.asAny("dialog"))
                                .prop("aria-labelledby", Js.asAny(id + "-title")),
                        DOM.h3(new HtmlProps().id(id + "-title"), title),
                        DOM.p(detail),
                        DOM.button(
                                new BtnProps()
                                        .id(id + "-close")
                                        .className(buttons.style(), buttons.quiet())
                                        .prop("popovertarget", Js.asAny(panel))
                                        .prop("popovertargetaction", Js.asAny("hide")),
                                "Close details")));
    }

    @UtilityStyles(Utilities.class)
    @CssImport(Defaults.class)
    abstract static class Styles {
        static Styles get() {
            return Workspace_Styled_Styles.get();
        }

        @UtilityCss("page flex flex-col items-start gap-6 p-6 text-ink")
        abstract String style();

        @UtilityCss("flex gap-3")
        abstract String controls();

        @UtilityCss("frame")
        abstract String frame();

        @UtilityCss("frame narrow")
        abstract String narrow();

        @UtilityCss("card flex flex-col items-start justify-between gap-4 py-6 note-min-wide:row")
        abstract String card();

        @UtilityCss("text-muted")
        abstract String muted();

        @UtilityCss("panel flex-col gap-4 p-5 bg-white text-ink transition-opacity opacity-1 starting:opacity-0"
                + " reduce-motion:transition-none")
        abstract String panel();
    }

    @UtilityStyles(Utilities.class)
    @Css("""
        border-radius:.375rem;
        &:focus-visible { outline:3px solid #145c4b; outline-offset:3px; }
        """)
    abstract static class Buttons {
        static Buttons get() {
            return Workspace_Styled_Buttons.get();
        }

        @UtilityCss("button px-4 py-2")
        abstract String style();

        @UtilityCss("bg-accent text-white hover:bg-hover")
        abstract String ready();

        @UtilityCss("bg-saved text-white")
        abstract String saved();

        @UtilityCss("bg-tint text-ink")
        abstract String quiet();

        @UtilityCss("trigger")
        abstract String trigger();
    }

    @GlobalCss("""
        * { box-sizing:border-box; }
        body { margin:0; font-family:system-ui,sans-serif; font-size:16px; line-height:1.5;
               background:#f7f8f5; color:#172b29; }
        h1,h2,h3,p { margin:0; }
        h1 { font-size:clamp(2rem,5vw,3rem); line-height:1.15; }
        h2,h3 { font-size:1.125rem; line-height:1.4; }
        button { font:inherit; }
        """)
    static class Defaults {}
}
