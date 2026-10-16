package react4j.todomvc.sting.ioc;

import react4j.todomvc.sting.model.ModelFragment;
import react4j.todomvc.sting.views.ViewsFragment;
import sting.Injector;

@Injector(includes = {ViewsFragment.class, ModelFragment.class})
public interface TodoInjector {
    @SuppressWarnings("UnusedReturnValue")
    static TodoInjector create() {
        return new Sting_TodoInjector();
    }
}
