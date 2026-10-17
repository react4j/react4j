package react4j.webspeechdemo;

import react4j.css.Css;

final class Styles {
    static final Title TITLE = new Styled_Styles_0024_Title();
    static final Form FORM = new Styled_Styles_0024_Form();
    static final Argument ARGUMENT = new Styled_Styles_0024_Argument();
    static final Label LABEL = new Styled_Styles_0024_Label();
    static final Range RANGE = new Styled_Styles_0024_Range();
    static final Select SELECT = new Styled_Styles_0024_Select();
    static final Reset RESET = new Styled_Styles_0024_Reset();
    static final Actions ACTIONS = new Styled_Styles_0024_Actions();
    static final Action ACTION = new Styled_Styles_0024_Action();
    static final Editor EDITOR = new Styled_Styles_0024_Editor();
    static final Text TEXT = new Styled_Styles_0024_Text();
    static final Marker MARKER = new Styled_Styles_0024_Marker();

    private Styles() {}

    @Css("""
        max-width: 480px;
        margin: 2rem auto;
        font-size: 150%;
        """)
    abstract static class Title {
        abstract String style();
    }

    @Css("""
        max-width: 480px;
        margin: 0 auto;
        """)
    abstract static class Form {
        abstract String style();
    }

    @Css("""
        height: 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
        """)
    abstract static class Argument {
        abstract String style();
    }

    @Css("""
        display: inline-block;
        width: 20%;
        """)
    abstract static class Label {
        abstract String style();
    }

    @Css("""
        display: inline-block;
        width: calc(80% - 3rem);
        margin: 0;
        -moz-appearance: none;
        &::-moz-range-thumb {
          background-color: #fff;
          height: 1rem;
          width: .5rem;
          border-color: #fff;
          box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
        }
        &::-moz-range-track {
          background-color: #fff;
        }
        """)
    abstract static class Range {
        abstract String style();
    }

    @Css("""
        display: inline-block;
        width: calc(80% - 3rem);
        margin: 0;
        appearance: none;
        """)
    abstract static class Select {
        abstract String style();
    }

    @Css("""
        width: 1.5rem;
        height: 1.5rem;
        margin: 0 0 0 1.5rem;
        display: inline-block;
        padding: 0;
        """)
    abstract static class Reset {
        abstract String style();
    }

    @Css("""
        margin: 1rem 0;
        display: flex;
        justify-content: space-between;
        """)
    abstract static class Actions {
        abstract String style();
    }

    @Css("""
        height: 3.25rem;
        width: 3.25rem;
        padding: 0;
        """)
    abstract static class Action {
        abstract String style();
    }

    @Css("""
        width: 100%;
        height: 8rem;
        display: block;
        margin-bottom: 1rem;
        border-radius: 5px;
        position: relative;
        background-color: #fff;
        transition: background-color 100ms ease;
        &.speaking {
          background-color: #333;
        }
        """)
    abstract static class Editor {
        abstract String style();

        abstract String speaking();
    }

    @Css("""
        position: absolute;
        display: block;
        border: none;
        resize: none;
        top: 0;
        left: 0;
        width: calc(100% - 1rem);
        height: calc(100% - 1rem);
        margin: .5rem;
        padding: 0;
        white-space: pre-wrap;
        line-height: 1.1rem;
        background-color: transparent;
        font-size: 12px;
        font-family: monospace;
        &.spoken {
          color: #aaa;
          overflow: auto;
        }
        """)
    abstract static class Text {
        abstract String style();

        abstract String spoken();
    }

    @Css("""
        position: fixed;
        border: 1px solid orange;
        display: none;
        margin: .5rem;
        padding: 0;
        background-color: transparent;
        transition: all 0s ease 0s;
        top: var(--marker-top);
        left: var(--marker-left);
        width: var(--marker-width);
        height: var(--marker-height);
        &.visible {
          display: block;
          transition: all 50ms ease;
        }
        """)
    abstract static class Marker {
        abstract String style();

        abstract String visible();
    }
}
