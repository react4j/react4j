package react4j.drumloop.views;

import react4j.css.Css;

final class Styles {
    static final Container CONTAINER = new Styled_Styles_0024_Container();
    static final Header HEADER = new Styled_Styles_0024_Header();
    static final Logo LOGO = new Styled_Styles_0024_Logo();
    static final FxButton FX_BUTTON = new Styled_Styles_0024_FxButton();
    static final Buttons BUTTONS = new Styled_Styles_0024_Buttons();
    static final Bpm BPM = new Styled_Styles_0024_Bpm();
    static final Play PLAY = new Styled_Styles_0024_Play();
    static final Sequencer SEQUENCER = new Styled_Styles_0024_Sequencer();
    static final Indicators INDICATORS = new Styled_Styles_0024_Indicators();
    static final Indicator INDICATOR = new Styled_Styles_0024_Indicator();
    static final Track TRACK = new Styled_Styles_0024_Track();
    static final TrackInfo TRACK_INFO = new Styled_Styles_0024_TrackInfo();
    static final TrackTitle TRACK_TITLE = new Styled_Styles_0024_TrackTitle();
    static final Steps STEPS = new Styled_Styles_0024_Steps();
    static final Step STEP = new Styled_Styles_0024_Step();

    private Styles() {}

    @Css("""
        max-width: 800px;
        margin: auto;
        background: linear-gradient(to bottom right, #222, #0a0a0a);
        border: 2px solid black;
        border-radius: 4px;
        margin-top: 20px;
        display: flex;
        flex-direction: column;
        """)
    abstract static class Container {
        abstract String style();
    }

    @Css("""
        flex: 0 1 auto;
        display: flex;
        justify-content: space-between;
        flex-direction: row;
        align-items: center;
        """)
    abstract static class Header {
        abstract String style();
    }

    @Css("""
        font-size: 28px;
        color: #25ccf7;
        font-family: 'Righteous', cursive;
        padding: 20px;
        margin: 0;
        text-transform: uppercase;
        display: inline-block;
        """)
    abstract static class Logo {
        abstract String style();
    }

    @Css("""
        background: none;
        flex: 1;
        border: 2px solid #fd7272;
        color: #fd7272;
        display: inline-block;
        font-size: 24px;
        border-radius: 2px;
        padding: 20px;
        font-family: 'Righteous', cursive;
        margin: 2px;

        &.held {
          background: #fd7272;
          color: black;
        }

        &:active {
          background: #fd7272;
          color: black;
        }
        """)
    abstract static class FxButton {
        abstract String style();

        abstract String held();
    }

    @Css("""
        flex: 1;
        flex-direction: row;
        align-items: stretch;
        width: 100%;
        padding: 0 20px 10px;
        display: flex;
        """)
    abstract static class Buttons {
        abstract String style();
    }

    @Css("""
        color: #25ccf7;
        border: 2px solid #25ccf7;
        font-size: 18px;
        background: none;
        padding: 10px;
        font-family: 'Righteous', cursive;
        border-radius: 2px;
        margin: 2px 20px 2px 4px;
        align-self: center;
        """)
    abstract static class Bpm {
        abstract String style();
    }

    @Css("""
        color: #FD7272;
        border: 2px solid #FD7272;
        background: none;
        font-family: 'Righteous', cursive;
        padding: 10px;
        font-size: 18px;
        border-radius: 2px;
        margin: 2px 20px 2px 4px;
        align-self: center;
        min-width: 100px;

        &.off {
          color: #25CCF7;
          border: 2px solid #25CCF7;
        }
        """)
    abstract static class Play {
        abstract String style();

        abstract String off();
    }

    @Css("""
        display: flex;
        flex: 1;
        flex-direction: column;
        border: 1px solid #555;
        margin: 0 20px 20px;
        position: relative;
        """)
    abstract static class Sequencer {
        abstract String style();
    }

    @Css("""
        position: absolute;
        width: 600px;
        top: 0;
        right: 0;
        height: 100%;
        padding: 4px 2px;
        pointer-events: none;
        """)
    abstract static class Indicators {
        abstract String style();
    }

    @Css("""
        position: absolute;
        top: 0;
        width: 38px;
        height: 100%;
        background: #00ff0020;
        left: var(--indicator-left);
        """)
    abstract static class Indicator {
        abstract String style();
    }

    @Css("""
        display: flex;
        flex: 1;
        """)
    abstract static class Track {
        abstract String style();
    }

    @Css("""
        flex: 0 0 155px;
        background: linear-gradient(#292929, #111);
        border: 1px solid #555;
        """)
    abstract static class TrackInfo {
        abstract String style();
    }

    @Css("""
        color: white;
        font-size: 14px;
        margin: 0;
        vertical-align: middle;
        padding: 0 10px;
        line-height: 50px;
        """)
    abstract static class TrackTitle {
        abstract String style();
    }

    @Css("""
        display: flex;
        flex: 1;
        """)
    abstract static class Steps {
        abstract String style();
    }

    @Css("""
        flex: 1;
        background: #FD7272;
        opacity: 0.35;
        border-radius: 2px;
        margin: 2px;

        &.on {
          opacity: 1;
        }

        &.odd {
          background: #25CCF7;
        }
        """)
    abstract static class Step {
        abstract String style();

        abstract String on();

        abstract String odd();
    }
}
