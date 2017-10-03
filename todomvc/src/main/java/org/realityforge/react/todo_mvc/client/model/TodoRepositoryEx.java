package org.realityforge.react.todo_mvc.client.model;

import org.realityforge.arez.annotations.Computed;

public interface TodoRepositoryEx
  extends TodoRepositoryExtension
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
}
