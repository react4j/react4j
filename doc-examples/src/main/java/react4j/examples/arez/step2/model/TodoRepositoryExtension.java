package react4j.examples.arez.step2.model;

import arez.annotations.Memoize;

public interface TodoRepositoryExtension
{
  @Memoize
  default int totalCount()
  {
    return self().findAll().size();
  }

  @Memoize
  default int activeCount()
  {
    return (int) self().findAll().stream().filter( todo -> !todo.isCompleted() ).count();
  }

  @Memoize
  default int completedCount()
  {
    return totalCount() - activeCount();
  }

  TodoRepository self();
}
