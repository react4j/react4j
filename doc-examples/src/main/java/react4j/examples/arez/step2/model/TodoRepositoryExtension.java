package react4j.examples.arez.step2.model;

import arez.annotations.Computed;

public interface TodoRepositoryExtension
{
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

  TodoRepository self();
}
