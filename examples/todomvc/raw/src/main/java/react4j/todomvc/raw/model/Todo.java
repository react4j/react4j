package react4j.todomvc.raw.model;

import arez.SafeProcedure;
import java.util.ArrayList;
import react4j.Keyed;

public final class Todo implements Keyed {
    private final String _id;
    private String _title;
    private boolean _completed;
    private final ArrayList<SafeProcedure> _subscribers = new ArrayList<>();

    Todo(final String id, final String title, final boolean completed) {
        _id = id;
        _title = title;
        _completed = completed;
    }

    @Override
    public String getKey() {
        return getId();
    }

    public String getId() {
        return _id;
    }

    public String getTitle() {
        return _title;
    }

    void setTitle(final String title) {
        _title = title;
        notifySubscribers();
    }

    public boolean isCompleted() {
        return _completed;
    }

    void setCompleted(final boolean completed) {
        _completed = completed;
        notifySubscribers();
    }

    void toggle() {
        setCompleted(!isCompleted());
    }

    boolean shouldShowTodo(final FilterMode filterMode) {
        if (FilterMode.ALL == filterMode) {
            return true;
        } else if (FilterMode.ACTIVE == filterMode) {
            return !isCompleted();
        } else {
            return isCompleted();
        }
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
