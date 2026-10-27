package react4j.examples.input_enhancers;

import react4j.Renderable;
import react4j.annotations.Render;
import react4j.annotations.View;

@View(type = View.Type.STATEFUL)
abstract class NoticeHost {
    @Render
    Renderable render() {
        return NoticeBuilder.message("Saved");
    }
}
