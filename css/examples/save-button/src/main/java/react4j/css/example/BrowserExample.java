package react4j.css.example;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.Nullable;
import react4j.css.Css;
import react4j.css.CssAnimation;
import react4j.css.CssCustomProperty;
import react4j.css.CssId;
import react4j.css.CssImport;
import react4j.css.CssStable;
import react4j.css.GlobalCss;
import react4j.css.Keyframes;
import react4j.css.UtilityCss;
import react4j.css.UtilityCssLibrary;
import react4j.css.UtilityStyles;
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
        StateStyle<String> buttonStates = button;
        StateStyle<String> statusStates = status;
        var custom = new CssProps().customProperty(buttonStates.successProperty(), success);
        ReactDOM.render(
                DOM.div(
                        DOM.span(
                                new HtmlProps()
                                        .id("utility")
                                        .className(UtilityOnly.get().style()),
                                "Utility"),
                        DOM.span(
                                new HtmlProps()
                                        .id("utility-hybrid")
                                        .className(UtilityHybrid.get().style()),
                                "Hybrid"),
                        DOM.span(
                                new HtmlProps()
                                        .id("utility-second")
                                        .className(UtilitySecond.get().style()),
                                "Second library"),
                        DOM.span(
                                new HtmlProps()
                                        .id("utility-important")
                                        .data("utility-important", "true")
                                        .className(UtilityHybrid.get().style()),
                                "Important global"),
                        DOM.div(
                                new HtmlProps()
                                        .id("utility-reset")
                                        .data("utility-reset", "true")
                                        .className(UtilityOnly.get().style()),
                                "Explicit reset"),
                        DOM.button(
                                new BtnProps()
                                        .id("modifier-only")
                                        .className(ModifierOnly.get().activeForRender()),
                                "Modifier through an interface helper"),
                        DOM.button(
                                new BtnProps()
                                        .id(Defaults.get().base())
                                        .data("shared", shared)
                                        .className(button.style()),
                                "Save"),
                        DOM.button(new BtnProps().id("success").className(buttonStates.success()), "Saved"),
                        DOM.button(new BtnProps().id("cancel").className(button.cancel()), "Cancel"),
                        DOM.button(
                                new BtnProps()
                                        .id("custom")
                                        .className(buttonStates.success())
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
                                DOM.span(new HtmlProps().id("status").className(statusStates.success()), "Status")),
                        DOM.button(new BtnProps().id("busy-button").className(buttonStates.forBusy(true)), "Saving"),
                        DOM.span(
                                new HtmlProps().id("busy-status").className(statusStates.forBusy(true)),
                                "Saving status"),
                        DOM.button(new BtnProps().id("peer-button").className(button.style()), "Focus to show status"),
                        DOM.span(
                                new HtmlProps()
                                        .id("peer-label")
                                        .data("peer", "true")
                                        .className(status.style()),
                                "Ready"),
                        DOM.span(
                                new HtmlProps().id(EqualNames.get().id()).data("symbol", "equal-names"),
                                "Stable ID sharing an animation's readable name"),
                        DOM.span(
                                new HtmlProps()
                                        .id("property-aliases")
                                        .data("symbol", "property-aliases")
                                        .className(CollisionStyles.get().first()),
                                "Distinct canonical properties"),
                        DOM.span(
                                new HtmlProps().id(ImportedIds.get().zLive()).data("symbol", "shared-import"),
                                "Checked shared ID"),
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

    @UtilityCssLibrary("""
        :root { --gutter:6px; }
        @layer first,second;
        @layer first { .tone {color:rgb(70, 80, 90);} }
        @layer second { .tone {color:rgb(30, 60, 90);} }
        .tone {padding-left:var(--gutter);}
        """)
    static final class UtilityLibraryA {}

    @UtilityCssLibrary("""
        @layer second,first;
        @layer first { .tone {color:rgb(100, 110, 120);} }
        @layer second { .tone {color:rgb(130, 140, 150);} }
        """)
    static final class UtilityLibraryB {}

    @UtilityStyles(UtilityLibraryA.class)
    abstract static class UtilityOnly {
        static UtilityOnly get() {
            return BrowserExample_Styled_UtilityOnly.get();
        }

        @UtilityCss("tone")
        abstract String style();
    }

    @UtilityStyles(UtilityLibraryA.class)
    @Css("""
        color:rgb(11, 22, 33);
        """)
    abstract static class UtilityHybrid {
        static UtilityHybrid get() {
            return BrowserExample_Styled_UtilityHybrid.get();
        }

        @UtilityCss("tone")
        abstract String style();
    }

    @UtilityStyles(UtilityLibraryB.class)
    abstract static class UtilitySecond {
        static UtilitySecond get() {
            return BrowserExample_Styled_UtilitySecond.get();
        }

        @UtilityCss("tone")
        abstract String style();
    }

    @CssImport(value = UtilityLibraryA.class, property = "gutter")
    @GlobalCss("""
        :root {--gutter:13px!important;}
        [data-utility-reset] {margin-left:23px;}
        [data-utility-important] {color:rgb(180, 20, 30)!important;}
        """)
    static final class UtilityReset {}

    interface StateStyle<T> {
        String style();

        String success();

        String busy();

        @CssCustomProperty(name = "success")
        T successProperty();

        default String forBusy(boolean busy) {
            return busy ? busy() : style();
        }
    }

    interface ActiveStyle<T> {
        @CssStable
        @JsMethod(name = "activeStyle")
        T active();

        default T activeForRender() {
            return active();
        }
    }

    @CssImport(value = DeadPropertyDefaults.class, property = "padding")
    @Css("""
        @keyframes unused {
          to { opacity: 0.25; }
        }
        color: rgb(12, 34, 56);
        &.unused { outline-width: 17px; padding: var(--padding); animation: unused 1s; }
        &.active { border-width: 7px; }
        """)
    abstract static class ModifierOnly implements ActiveStyle<String> {
        static ModifierOnly get() {
            return BrowserExample_Styled_ModifierOnly.get();
        }

        abstract String style();

        abstract String unused();
    }

    @CssStable
    @Css("""
        color: rgb(98, 76, 54);
        """)
    abstract static class UnusedOwner {
        abstract String style();
    }

    @GlobalCss("""
        :root { --padding: 47px; }
        """)
    static final class DeadPropertyDefaults {}

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
    abstract static class SaveButton implements StateStyle<String> {
        static SaveButton get() {
            return BrowserExample_Styled_SaveButton.get();
        }

        abstract String cancel();

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
    abstract static class Status implements StateStyle<String> {
        static Status get() {
            return BrowserExample_Styled_Status.get();
        }

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

    @CssStable
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

    @CssImport(name = "pulse", value = EqualNamePulse.class)
    @GlobalCss("""
        #jcss-react4j-css-example-BrowserExample_0024_EqualNamePulse--keyframes { border-top: 11px solid; }
        """)
    abstract static class EqualNames {
        static EqualNames get() {
            return BrowserExample_Styled_EqualNames.get();
        }

        @CssStable
        @CssId(name = "jcss-react4j-css-example-BrowserExample_0024_EqualNamePulse--keyframes")
        abstract String id();

        @CssAnimation(name = "pulse")
        abstract String animation();
    }

    @Keyframes("""
        to { opacity: 0.12345; }
        """)
    abstract static class EqualNamePulse {}

    @GlobalCss("""
        #shared-import { padding-left: 13px; }
        """)
    abstract static class FirstIds {
        @CssStable
        @CssId(name = "shared-import")
        abstract String id();
    }

    @GlobalCss("""
        #shared-import { padding-right: 37px; }
        """)
    abstract static class SecondIds {
        @CssStable
        @CssId(name = "shared-import")
        abstract String id();
    }

    @GlobalCss("""
        #shared-import { margin-right: 29px; }
        """)
    abstract static class ActiveIds {
        @CssStable
        @CssId(name = "shared-import")
        abstract String id();
    }

    @GlobalCss("""
        [data-symbol=shared-import] { border-left: 23px solid; }
        """)
    static final class IdRequirements {}

    @CssImport(IdRequirements.class)
    @CssImport(value = FirstIds.class, id = "shared-import", name = "local")
    @GlobalCss("""
        #local { margin-left: 19px; }
        """)
    static final class IdAliases {}

    @CssImport(ActiveIds.class)
    @CssImport(value = IdAliases.class, id = "local", name = "first")
    @CssImport(value = SecondIds.class, id = "shared-import", name = "second")
    @GlobalCss("""
        #second { font-weight: 700; }
        """)
    abstract static class ImportedIds {
        static ImportedIds get() {
            return BrowserExample_Styled_ImportedIds.get();
        }

        @CssId(name = "second")
        abstract String aDead();

        @CssId(name = "first")
        abstract String zLive();
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
    abstract static class Defaults {
        static Defaults get() {
            return BrowserExample_Styled_Defaults.get();
        }

        @CssStable
        @CssId
        abstract String base();
    }

    @GlobalCss("""
        :root { --accent: 13px; }
        """)
    static final class CollisionDefaults {}

    @CssStable
    @GlobalCss("""
        [data-symbol=property-aliases] {
          --jcss-react4j-css-example-BrowserExample_0024_CollisionDefaults--accent: 37px;
          border-right: 37px solid;
        }
        """)
    static final class CollisionPublicNames {}

    @CssImport(value = CollisionDefaults.class, property = "accent", name = "first")
    @CssImport(
            value = CollisionPublicNames.class,
            property = "jcss-react4j-css-example-BrowserExample_0024_CollisionDefaults--accent",
            name = "second")
    @Css("""
        &.first { padding-left: var(--first); }
        &.second { padding-right: var(--second); }
        """)
    abstract static class CollisionStyles {
        static CollisionStyles get() {
            return BrowserExample_Styled_CollisionStyles.get();
        }

        abstract String style();

        abstract String first();

        abstract String second();
    }

    @CssImport(Defaults.class)
    @CssImport(value = Defaults.class, id = "base")
    @CssImport(value = Defaults.class, property = "theme-color")
    @GlobalCss("""
        :root { --theme-color: #909; }
        #base { border-top-width: 3px; }
        """)
    static final class Theme {}

    @CssImport(Defaults.class)
    @CssImport(value = Defaults.class, id = "base")
    @CssImport(value = Defaults.class, property = "theme-color")
    @GlobalCss("""
        :root { --theme-color: #099; }
        #base { border-top-width: 4px; }
        """)
    static final class AlternateTheme {}

    @CssImport(value = Defaults.class, id = "base")
    @GlobalCss("""
        #base { --excluded: unwanted; }
        """)
    static final class UnusedGlobals {}
}
