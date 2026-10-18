package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.drumloop.model.Track;

@View
abstract class TrackView {
    final Track _track;

    TrackView(@Input final Track track) {
        _track = track;
    }

    @Render
    ReactNode render() {
        _track.suspendUntilAudioLoaded();
        return div(
                new HtmlProps().className(TrackView_Styled_TrackStyle.get().style()),
                div(
                        new HtmlProps()
                                .className(TrackView_Styled_TrackInfoStyle.get().style()),
                        h2(
                                new HtmlProps()
                                        .className(TrackView_Styled_TrackTitleStyle.get()
                                                .style()),
                                _track.getName())),
                div(
                        new HtmlProps()
                                .className(TrackView_Styled_StepsStyle.get().style()),
                        fragment(_track.getStepCells().stream().map(StepButtonBuilder::cell))));
    }

    @Css("""
        display: flex;
        flex: 1;
        """)
    abstract static class TrackStyle {
        abstract String style();
    }

    @Css("""
        box-sizing: border-box;
        flex: 0 0 155px;
        background: linear-gradient(#292929, #111);
        border: 1px solid #555;
        """)
    abstract static class TrackInfoStyle {
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
    abstract static class TrackTitleStyle {
        abstract String style();
    }

    @Css("""
        display: flex;
        flex: 1;
        """)
    abstract static class StepsStyle {
        abstract String style();
    }
}
