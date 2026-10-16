package react4j.todomvc.sting.model;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import java.util.stream.Collectors;

@ArezComponent(service = Feature.ENABLE, requireId = Feature.DISABLE, disposeNotifier = Feature.DISABLE)
public abstract class TodoService {
    private final TodoRepository _repository;

    TodoService(final TodoRepository repository) {
        _repository = repository;
    }

    public void addTodo(final String title) {
        _repository.create(title, false);
    }

    @Action
    public void toggleAll(final boolean completed) {
        _repository.findAll().forEach(todo -> todo.setCompleted(completed));
    }

    @Action
    public void save(final Todo todo, final String newTitle) {
        todo.setTitle(newTitle);
    }

    @Action
    public void clearCompleted() {
        _repository
                .entities()
                // Find all completed
                .filter(Todo::isCompleted)
                .collect(Collectors.toList())
                // Have to collect() to create new list so can mutate
                // by destroy in following line
                .forEach(_repository::destroy);
    }
}
