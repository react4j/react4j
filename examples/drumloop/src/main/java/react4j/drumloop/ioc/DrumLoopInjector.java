package react4j.drumloop.ioc;

import react4j.drumloop.model.ModelFragment;
import react4j.drumloop.views.ViewsFragment;
import sting.Injector;

@SuppressWarnings("UnusedReturnValue")
@Injector(includes = {ModelFragment.class, ViewsFragment.class})
public interface DrumLoopInjector {
    static DrumLoopInjector create() {
        return new Sting_DrumLoopInjector();
    }
}
