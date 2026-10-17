package react4j.hrm;

import static react4j.dom.DOM.*;

import arez.annotations.CascadeDispose;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.ImgProps;

@View(type = View.Type.TRACKING)
abstract class Application {
    @CascadeDispose
    final HeartRateMonitor _heartRateMonitor = HeartRateMonitor.create();

    @Render
    ReactNode render() {
        final int heartRate = _heartRateMonitor.getHeartRate();
        final boolean connected = _heartRateMonitor.isConnected();

        return div(
                new HtmlProps().className(Styles.CONTAINER.style()),
                div(
                        new HtmlProps().className(Styles.PANEL.style()),
                        h1("Heart Rate Monitor"),
                        img(new ImgProps()
                                .className(Styles.HEART.style(), heartRate > 0 ? "beating" : null)
                                .src("img/heart.svg")),
                        0 != heartRate ? ReactNode.of(heartRate) : null,
                        button(new BtnProps().disabled(connected).onClick(e -> _heartRateMonitor.connect()), "Start"),
                        button(
                                new BtnProps().disabled(!connected).onClick(e -> _heartRateMonitor.disconnect()),
                                "Stop")));
    }
}
