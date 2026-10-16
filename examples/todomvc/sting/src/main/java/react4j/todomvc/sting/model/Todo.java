package react4j.todomvc.sting.model;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.Observable;

@ArezComponent(requireId = Feature.ENABLE, requireEquals = Feature.ENABLE, observable = Feature.ENABLE)
public abstract class Todo {
    private String _title;
    private boolean _completed;

    Todo(final String title, final boolean completed) {
        _title = title;
        _completed = completed;
    }

    @Observable
    public String getTitle() {
        return _title;
    }

    void setTitle(final String title) {
        _title = title;
    }

    @Observable
    public boolean isCompleted() {
        return _completed;
    }

    void setCompleted(final boolean completed) {
        _completed = completed;
    }

    @Action
    public void toggle() {
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
}
