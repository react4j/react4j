package react4j.todomvc.arez.model;

import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.Feature;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import arez.annotations.Observe;
import java.util.List;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

@ArezComponent(requireId = Feature.DISABLE, disposeNotifier = Feature.DISABLE)
public abstract class ViewService {
    private final TodoRepository _todoRepository;
    private final BrowserLocation _browserLocation;

    ViewService(final TodoRepository todoRepository, final BrowserLocation browserLocation) {
        _todoRepository = todoRepository;
        _browserLocation = browserLocation;
    }

    @Observable
    @Nullable
    @ComponentDependency(action = ComponentDependency.Action.SET_NULL)
    public abstract Todo getTodoBeingEdited();

    public abstract void setTodoBeingEdited(@Nullable Todo todoBeingEdited);

    @Memoize
    public FilterMode getFilterMode() {
        final String location = _browserLocation.getLocation();
        if ("active".equals(location)) {
            return FilterMode.ACTIVE;
        } else if ("completed".equals(location)) {
            return FilterMode.COMPLETED;
        } else {
            return FilterMode.ALL;
        }
    }

    @Memoize
    public List<Todo> filteredTodos() {
        final FilterMode filterMode = getFilterMode();
        return _todoRepository
                .entities()
                .filter(todo -> todo.shouldShowTodo(filterMode))
                .collect(Collectors.toList());
    }

    @Observe(mutation = true, nestedActionsAllowed = true)
    void cleanLocation() {
        final String browserLocation = _browserLocation.getBrowserLocation();
        if (isValid(browserLocation)) {
            _browserLocation.changeLocation(browserLocation);
        } else if (isValid(_browserLocation.getLocation())) {
            _browserLocation.resetBrowserLocation();
        } else {
            _browserLocation.changeLocation("");
        }
    }

    private boolean isValid(final String location) {
        return "active".equals(location) || "completed".equals(location) || "".equals(location);
    }
}
