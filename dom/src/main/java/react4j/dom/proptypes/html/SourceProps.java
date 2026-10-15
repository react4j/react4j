package react4j.dom.proptypes.html;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

/**
 * Props for source elements. Refer to http://www.w3schools.com/tags/tag_source.asp
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SourceProps extends HtmlGlobalFields<SourceProps> {
    @JsOverlay
    public final SourceProps media(@Nullable String s) {
        setMedia(s);
        return self();
    }

    @JsOverlay
    public final SourceProps src(@Nullable String s) {
        setSrc(s);
        return self();
    }

    @JsOverlay
    public final SourceProps type(String s) {
        setType(s);
        return self();
    }
}
