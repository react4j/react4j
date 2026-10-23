package react4j.utilitydemo;

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
abstract class SaveButton {
    private boolean _saved;

    @ScheduleRender
    abstract void scheduleRender();

    private void toggleSaved() {
        _saved = !_saved;
        scheduleRender();
    }

    @Render
    ReactNode render() {
        var button = ButtonStyles.get();
        return DOM.section(
                new HtmlProps().className(PageStyles.get().style()),
                DOM.h1("Utility CSS"),
                DOM.p("Reusable utility classes, checked from Java."),
                DOM.button(
                        new BtnProps()
                                .className(button.style(), _saved ? button.saved() : button.ready())
                                .onClick(e -> toggleSaved()),
                        _saved ? "Saved" : "Save changes"),
                DOM.p("Click the button to switch its style. Local CSS gives it the pill shape."));
    }

    @UtilityStyles(Utilities.class)
    @CssImport(Defaults.class)
    abstract static class PageStyles {
        static PageStyles get() {
            return SaveButton_Styled_PageStyles.get();
        }

        @UtilityCss("min-h-screen flex flex-col items-start justify-center gap-4 p-6 sm:p-10")
        abstract String style();
    }

    @UtilityStyles(Utilities.class)
    @Css("""
        border-radius: 999px;
        &:focus-visible { outline: 3px solid #93c5fd; outline-offset: 3px; }
        """)
    abstract static class ButtonStyles {
        static ButtonStyles get() {
            return SaveButton_Styled_ButtonStyles.get();
        }

        @UtilityCss("inline-flex items-center border-0 rounded-md px-4 py-2 text-white font-semibold cursor-pointer")
        abstract String style();

        @UtilityCss("bg-blue-700 hover:bg-blue-800")
        abstract String ready();

        @UtilityCss("bg-green-700 hover:bg-green-800")
        abstract String saved();
    }

    @GlobalCss("""
        * { box-sizing: border-box; }
        body { margin: 0; font-family: system-ui, sans-serif; color: #0f172a; background: #f8fafc; }
        h1, p { margin: 0; }
        button { font: inherit; }
        """)
    static class Defaults {}
}
