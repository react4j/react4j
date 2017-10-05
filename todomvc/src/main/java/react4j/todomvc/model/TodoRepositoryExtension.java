package react4j.todomvc.model;

import java.util.List;
import org.realityforge.arez.annotations.Computed;

public interface TodoRepositoryExtension
  extends TodoBaseRepositoryExtension
{
  @Computed
  default boolean isEmpty()
  {
    return 0 == totalCount();
  }

  default boolean isNotEmpty()
  {
    return !isEmpty();
  }

  @Computed
  default int totalCount()
  {
    return self().findAll().size();
  }

  @Computed
  default int activeCount()
  {
    return (int) self().findAll().stream().filter( todo -> !todo.isCompleted() ).count();
  }

  @Computed
  default int completedCount()
  {
    return totalCount() - activeCount();
  }

  @Computed
  default List<Todo> filteredTodos()
  {
    final FilterMode filterMode = AppData.viewService.getFilterMode();
    return self().toList( self().entities().stream().filter( todo -> todo.shouldShowTodo( filterMode ) ) );
  }
}
