package react4j.hrm;

import static react4j.dom.DOM.*;

import arez.annotations.CascadeDispose;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.ImgProps;

@View(type = View.Type.TRACKING)
abstract class Application {
    private static final ContainerStyle CONTAINER = new Styled_Application_0024_ContainerStyle();
    private static final PanelStyle PANEL = new Styled_Application_0024_PanelStyle();
    private static final HeartStyle HEART = new Styled_Application_0024_HeartStyle();

    @CascadeDispose
    final HeartRateMonitor _heartRateMonitor = HeartRateMonitor.create();

    @Render
    ReactNode render() {
        final int heartRate = _heartRateMonitor.getHeartRate();
        final boolean connected = _heartRateMonitor.isConnected();

        return div(
                new HtmlProps().className(CONTAINER.style()),
                div(
                        new HtmlProps().className(PANEL.style()),
                        h1("Heart Rate Monitor"),
                        img(new ImgProps()
                                .className(heartRate > 0 ? HEART.beating() : HEART.style())
                                .src("img/heart.svg")),
                        0 != heartRate ? ReactNode.of(heartRate) : null,
                        button(new BtnProps().disabled(connected).onClick(e -> _heartRateMonitor.connect()), "Start"),
                        button(
                                new BtnProps().disabled(!connected).onClick(e -> _heartRateMonitor.disconnect()),
                                "Stop")));
    }

    @Css("""
        display: flex;
        justify-content: center;
        """)
    abstract static class ContainerStyle {
        abstract String style();
    }

    @Css("""
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-between;
        """)
    abstract static class PanelStyle {
        abstract String style();
    }

    @Css("""
        width: 10em;

        @keyframes beat {
          from { transform: scale(1); }
          to { transform: scale(0.9); }
        }

        &.beating {
          animation-name: beat;
          animation-direction: alternate;
          animation-iteration-count: infinite;
          animation-timing-function: ease-in-out;
          animation-duration: 0.8s;
        }
        """)
    abstract static class HeartStyle {
        abstract String style();

        abstract String beating();
    }
}
