package react4j.css.example;

import react4j.ReactNode;
import react4j.css.CssCustomProperty;
import react4j.css.UtilityCss;
import react4j.css.UtilityCssLibrary;
import react4j.css.UtilityStyles;
import react4j.dom.DOM;
import react4j.dom.ReactDOM;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;

/** Browser fixture for rules compiled by the JVM utility language. */
public final class NativeExample {
    private NativeExample() {}

    public static void render(Object container) {
        var styles = Styles.get();
        ReactDOM.render(
                DOM.div(
                        DOM.div(
                                new HtmlProps().id("native-frame").className(styles.frame()),
                                DOM.div(
                                        new HtmlProps().id("native-query").className(styles.query()),
                                        "Container child")),
                        DOM.div(
                                new HtmlProps().id("native-scroll").className(styles.scroller()),
                                DOM.div(
                                        new HtmlProps()
                                                .id("native-scroll-child")
                                                .className(styles.scrollChild()),
                                        "Scrollable child")),
                        DOM.div(new HtmlProps().id("native-enter").className(styles.enter()), "Entering")),
                container);
    }

    public static void renderPopovers(Object container) {
        ReactDOM.render(
                DOM.div(popover("one", false), popover("two", false), popover("edge", false), popover("large", true)),
                container);
    }

    private static ReactNode popover(String id, boolean large) {
        var styles = Styles.get();
        return DOM.div(
                new HtmlProps().className(styles.scope()),
                DOM.button(new BtnProps().id("native-trigger-" + id).className(styles.trigger()), "Open " + id),
                DOM.div(
                        new HtmlProps()
                                .id("native-panel-" + id)
                                .className(large ? styles.largePanel() : styles.panel()),
                        DOM.div(
                                new HtmlProps().className(large ? styles.largeContent() : styles.content()),
                                "Panel " + id)));
    }

    public static String densityProperty() {
        return Rules.get().density();
    }

    @UtilityCssLibrary("""
        @utility-rules;
        @tokens thresholds <length> { small:150px; }
        @utility frame { container:card / inline-size; width:200px; --density:cozy; }
        @utility unused-setup { container:absent / inline-size; width:987px; }
        @utility scroller { container-type:scroll-state; height:60px; overflow:auto; }
        @utility spacer { height:240px; }
        @utility base { color:rgb(90,100,110); font-weight:400; }
        @utility green { color:rgb(10,120,30); }
        @utility bold { font-weight:700; }
        @utility scrolling { letter-spacing:7px; }
        @utility zero { opacity:0; }
        @utility enter { opacity:1; transition:opacity 100s linear; }
        @utility popover-scope { anchor-scope:--trigger; }
        @utility popover-trigger { anchor-name:--trigger; position:fixed; width:96px; height:24px; }
        @utility popover-panel {
          position:fixed; position-anchor:--trigger; inset:auto; margin:0; padding:0; border:0;
          position-area:block-end span-inline-end;
          position-try-fallbacks:flip-block,flip-inline,flip-block flip-inline;
          inline-size:anchor-size(width); min-inline-size:0;
          max-inline-size:calc(100dvw - 2rem); max-block-size:calc(100dvh - 2rem); overflow:auto;
        }
        @utility popover-large { inline-size:200vw; block-size:200vh; }
        @utility popover-content { height:48px; }
        @utility popover-large-content { height:2000px; }
        @variant "wide-{v}" (v:tokens(thresholds)|arbitrary(<length>)) {
          @container card (inline-size >= param(v)) { @slot; }
        }
        @variant compact { @container card style(--density:compact) { @slot; } }
        @variant absent { @container absent (width > 0px) { @slot; } }
        @variant scrollable { @container scroll-state(scrollable:bottom) { @slot; } }
        @variant starting { @starting-style { @slot; } }
        """)
    abstract static class Rules {
        static Rules get() {
            return NativeExample_Styled_Rules.get();
        }

        @CssCustomProperty(name = "density")
        abstract String density();
    }

    @UtilityStyles(Rules.class)
    abstract static class Styles {
        static Styles get() {
            return NativeExample_Styled_Styles.get();
        }

        abstract String style();

        @UtilityCss("frame")
        abstract String frame();

        @UtilityCss("base wide-small:green compact:bold absent:bold")
        abstract String query();

        @UtilityCss("scroller")
        abstract String scroller();

        @UtilityCss("spacer scrollable:scrolling")
        abstract String scrollChild();

        @UtilityCss("popover-scope")
        abstract String scope();

        @UtilityCss("popover-trigger")
        abstract String trigger();

        @UtilityCss("popover-panel")
        abstract String panel();

        @UtilityCss("popover-panel popover-large")
        abstract String largePanel();

        @UtilityCss("popover-content")
        abstract String content();

        @UtilityCss("popover-large-content")
        abstract String largeContent();

        @UtilityCss("enter starting:zero")
        abstract String enter();
    }
}
