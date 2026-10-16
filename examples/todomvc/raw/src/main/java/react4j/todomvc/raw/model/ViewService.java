package react4j.todomvc.raw.model;

import arez.SafeProcedure;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import react4j.todomvc.browser.Event;
import react4j.todomvc.browser.Location;
import react4j.todomvc.browser.WindowGlobal;

public final class ViewService {
    private final List<SafeProcedure> _subscribers = new ArrayList<>();
    private final TodoRepository _todoRepository;

    @Nullable
    private Todo _todoBeingEdited;

    ViewService(final TodoRepository todoRepository) {
        _todoRepository = todoRepository;
        WindowGlobal.addEventListener("hashchange", this::onHashChangeEvent, false);
        todoRepository.subscribe(this::updateTodoBeingEdited);
    }

    @Nullable
    public Todo getTodoBeingEdited() {
        return _todoBeingEdited;
    }

    public void setTodoBeingEdited(@Nullable final Todo todoBeingEdited) {
        _todoBeingEdited = todoBeingEdited;
        notifySubscribers();
    }

    public FilterMode getFilterMode() {
        final String location = getHash();
        if ("active".equals(location)) {
            return FilterMode.ACTIVE;
        } else if ("completed".equals(location)) {
            return FilterMode.COMPLETED;
        } else {
            return FilterMode.ALL;
        }
    }

    public List<Todo> filteredTodos() {
        final FilterMode filterMode = getFilterMode();
        return _todoRepository
                .entities()
                .filter(todo -> todo.shouldShowTodo(filterMode))
                .collect(Collectors.toList());
    }

    private void updateTodoBeingEdited() {
        final Todo todoBeingEdited = getTodoBeingEdited();
        if (null != todoBeingEdited && !_todoRepository.contains(todoBeingEdited)) {
            setTodoBeingEdited(null);
        }
    }

    private boolean isValid(final String location) {
        return "active".equals(location) || "completed".equals(location) || "".equals(location);
    }

    private void onHashChangeEvent(final Event e) {
        e.preventDefault();
        final String browserLocation = getHash();
        if (isValid(browserLocation)) {
            notifySubscribers();
        } else {
            /*
             * This code is needed to remove the stray #.
             * See https://stackoverflow.com/questions/1397329/how-to-remove-the-hash-from-window-location-url-with-javascript-without-page-r/5298684#5298684
             */
            final Location location = WindowGlobal.location();
            WindowGlobal.history()
                    .pushState("", WindowGlobal.document().title(), location.pathname() + location.search());
        }
    }

    private String getHash() {
        final String hash = WindowGlobal.location().hash();
        return hash.isEmpty() ? "" : hash.substring(1);
    }

    public void subscribe(final SafeProcedure subscriber) {
        _subscribers.add(subscriber);
    }

    public void unsubscribe(final SafeProcedure subscriber) {
        _subscribers.remove(subscriber);
    }

    private void notifySubscribers() {
        _subscribers.forEach(SafeProcedure::call);
    }
}
