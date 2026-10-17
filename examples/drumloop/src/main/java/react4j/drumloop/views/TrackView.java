package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
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
                new HtmlProps().className(Styles.TRACK.style()),
                div(
                        new HtmlProps().className(Styles.TRACK_INFO.style()),
                        h2(new HtmlProps().className(Styles.TRACK_TITLE.style()), _track.getName())),
                div(
                        new HtmlProps().className(Styles.STEPS.style()),
                        fragment(_track.getStepCells().stream().map(StepButtonBuilder::cell))));
    }
}
