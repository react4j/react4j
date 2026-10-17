package react4j.todomvc.styles;

import react4j.css.Css;

public final class Styles {
    public static final Heading HEADING = new Styled_Styles_0024_Heading();
    public static final Entry ENTRY = new Styled_Styles_0024_Entry();
    public static final ToggleAll TOGGLE_ALL = new Styled_Styles_0024_ToggleAll();
    public static final List LIST = new Styled_Styles_0024_List();
    public static final Item ITEM = new Styled_Styles_0024_Item();
    public static final View VIEW = new Styled_Styles_0024_View();
    public static final Toggle TOGGLE = new Styled_Styles_0024_Toggle();
    public static final Label LABEL = new Styled_Styles_0024_Label();
    public static final Destroy DESTROY = new Styled_Styles_0024_Destroy();
    public static final Edit EDIT = new Styled_Styles_0024_Edit();
    public static final Footer FOOTER = new Styled_Styles_0024_Footer();
    public static final Count COUNT = new Styled_Styles_0024_Count();
    public static final CountNumber COUNT_NUMBER = new Styled_Styles_0024_CountNumber();
    public static final Filters FILTERS = new Styled_Styles_0024_Filters();
    public static final FilterItem FILTER_ITEM = new Styled_Styles_0024_FilterItem();
    public static final FilterLink FILTER_LINK = new Styled_Styles_0024_FilterLink();
    public static final Clear CLEAR = new Styled_Styles_0024_Clear();

    private Styles() {}

    @Css("""
        position: absolute;
        top: -155px;
        width: 100%;
        font-size: 100px;
        font-weight: 100;
        text-align: center;
        color: rgba(175, 47, 47, 0.15);
        -webkit-text-rendering: optimizeLegibility;
        -moz-text-rendering: optimizeLegibility;
        text-rendering: optimizeLegibility;
        """)
    public abstract static class Heading {
        public abstract String style();
    }

    @Css("""
        position: relative;
        margin: 0;
        width: 100%;
        font-size: 24px;
        font-family: inherit;
        font-weight: inherit;
        line-height: 1.4em;
        outline: none;
        color: inherit;
        box-sizing: border-box;
        -webkit-font-smoothing: antialiased;
        -moz-font-smoothing: antialiased;
        font-smoothing: antialiased;
        padding: 16px 16px 16px 60px;
        border: none;
        background: rgba(0, 0, 0, 0.003);
        box-shadow: inset 0 -2px 1px rgba(0, 0, 0, 0.03);

        &::-webkit-input-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        &::-moz-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        &::input-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        """)
    public abstract static class Entry {
        public abstract String style();
    }

    @Css("""
        position: absolute;
        top: -55px;
        left: -12px;
        width: 60px;
        height: 34px;
        text-align: center;
        border: none;
        &:before {
          content: '\\276F';
          font-size: 22px;
          color: #e6e6e6;
          padding: 10px 27px 10px 27px;
        }
        &:checked:before {
          color: #737373;
        }
        @media screen and (-webkit-min-device-pixel-ratio: 0) {
          & {
            background: none;
            -webkit-transform: rotate(90deg);
            transform: rotate(90deg);
            -webkit-appearance: none;
            appearance: none;
          }
        }
        """)
    public abstract static class ToggleAll {
        public abstract String style();
    }

    @Css("""
        margin: 0;
        padding: 0;
        list-style: none;
        """)
    public abstract static class List {
        public abstract String style();
    }

    @Css("""
        position: relative;
        font-size: 24px;
        border-bottom: 1px solid #ededed;
        --todo-destroy-display: none;
        &:hover {
          --todo-destroy-display: block;
        }
        &:last-child {
          border-bottom: none;
        }
        &.editing {
          border-bottom: none;
          padding: 0;
        }
        &.editing:last-child {
          margin-bottom: -1px;
        }
        """)
    public abstract static class Item {
        public abstract String style();

        public abstract String editing();
    }

    @Css("""
        &.editing {
          display: none;
        }
        """)
    public abstract static class View {
        public abstract String style();

        public abstract String editing();
    }

    @Css("""
        text-align: center;
        width: 40px;
        height: auto;
        position: absolute;
        top: 0;
        bottom: 0;
        margin: auto 0;
        border: none;
        -webkit-appearance: none;
        appearance: none;
        &:after {
          content: url('toggle.svg');
        }
        &:checked:after {
          content: url('toggle_checked.svg');
        }
        @media screen and (-webkit-min-device-pixel-ratio: 0) {
          & {
            background: none;
            height: 40px;
          }
        }
        """)
    public abstract static class Toggle {
        public abstract String style();
    }

    @Css("""
        white-space: pre-line;
        word-break: break-all;
        padding: 15px 60px 15px 15px;
        margin-left: 45px;
        display: block;
        line-height: 1.2;
        transition: color 0.4s;
        &.completed {
          color: #d9d9d9;
          text-decoration: line-through;
        }
        """)
    public abstract static class Label {
        public abstract String style();

        public abstract String completed();
    }

    @Css("""
        display: var(--todo-destroy-display);
        position: absolute;
        top: 0;
        right: 10px;
        bottom: 0;
        width: 40px;
        height: 40px;
        margin: auto 0;
        font-size: 30px;
        color: #cc9a9a;
        margin-bottom: 11px;
        transition: color 0.2s ease-out;
        &:hover {
          color: #af5b5e;
        }
        &:after {
          content: '\\00D7';
        }
        """)
    public abstract static class Destroy {
        public abstract String style();
    }

    @Css("""
        position: relative;
        margin: 0;
        width: 100%;
        font-size: 24px;
        font-family: inherit;
        font-weight: inherit;
        line-height: 1.4em;
        outline: none;
        color: inherit;
        padding: 6px;
        border: 1px solid #999;
        box-shadow: inset 0 -1px 5px 0 rgba(0, 0, 0, 0.2);
        box-sizing: border-box;
        -webkit-font-smoothing: antialiased;
        -moz-font-smoothing: antialiased;
        font-smoothing: antialiased;
        display: none;
        &.editing {
          display: block;
          width: 506px;
          padding: 13px 17px 12px 17px;
          margin: 0 0 0 43px;
        }

        &::-webkit-input-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        &::-moz-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        &::input-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        """)
    public abstract static class Edit {
        public abstract String style();

        public abstract String editing();
    }

    @Css("""
        color: #777;
        padding: 10px 15px;
        height: 20px;
        text-align: center;
        border-top: 1px solid #e6e6e6;
        &:before {
          content: '';
          position: absolute;
          right: 0;
          bottom: 0;
          left: 0;
          height: 50px;
          overflow: hidden;
          box-shadow: 0 1px 1px rgba(0, 0, 0, 0.2),
            0 8px 0 -3px #f6f6f6,
            0 9px 1px -3px rgba(0, 0, 0, 0.2),
            0 16px 0 -6px #f6f6f6,
            0 17px 2px -6px rgba(0, 0, 0, 0.2);
        }
        @media (max-width: 430px) {
          & {
            height: 50px;
          }
        }
        """)
    public abstract static class Footer {
        public abstract String style();
    }

    @Css("""
        float: left;
        text-align: left;
        """)
    public abstract static class Count {
        public abstract String style();
    }

    @Css("""
        font-weight: 300;
        """)
    public abstract static class CountNumber {
        public abstract String style();
    }

    @Css("""
        margin: 0;
        padding: 0;
        list-style: none;
        position: absolute;
        right: 0;
        left: 0;
        @media (max-width: 430px) {
          & {
            bottom: 10px;
          }
        }
        """)
    public abstract static class Filters {
        public abstract String style();
    }

    @Css("""
        display: inline;
        """)
    public abstract static class FilterItem {
        public abstract String style();
    }

    @Css("""
        color: inherit;
        margin: 3px;
        padding: 3px 7px;
        text-decoration: none;
        border: 1px solid transparent;
        border-radius: 3px;
        &.selected, &:hover {
          border-color: rgba(175, 47, 47, 0.1);
        }
        &.selected {
          border-color: rgba(175, 47, 47, 0.2);
        }
        """)
    public abstract static class FilterLink {
        public abstract String style();

        public abstract String selected();
    }

    @Css("""
        float: right;
        position: relative;
        line-height: 20px;
        text-decoration: none;
        cursor: pointer;
        &:hover {
          text-decoration: underline;
        }
        """)
    public abstract static class Clear {
        public abstract String style();
    }
}
