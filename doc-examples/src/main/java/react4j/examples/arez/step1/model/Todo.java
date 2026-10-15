package react4j.examples.arez.step1.model;

import arez.annotations.ArezComponent;
import arez.annotations.ComponentIdRef;
import arez.annotations.Observable;

@ArezComponent
public abstract class Todo {
    private String _title;
    private boolean _completed;

    Todo(final String title, final boolean completed) {
        _title = title;
        _completed = completed;
    }

    @ComponentIdRef
    public abstract int getId();

    @Observable
    public String getTitle() {
        return _title;
    }

    public void setTitle(final String title) {
        _title = title;
    }

    @Observable
    public boolean isCompleted() {
        return _completed;
    }

    public void setCompleted(final boolean completed) {
        _completed = completed;
    }
}
