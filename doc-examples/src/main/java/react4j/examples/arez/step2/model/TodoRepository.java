package react4j.examples.arez.step2.model;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.Memoize;
import arez.component.internal.AbstractRepository;
import javax.annotation.Nonnull;

@ArezComponent( service = Feature.ENABLE )
public abstract class TodoRepository
  extends AbstractRepository<Integer, Todo, TodoRepository>
{
  @Action
  @Nonnull
  Todo create( @Nonnull final String title, final boolean completed )
  {
    final Todo entity = new Arez_Todo( title, completed );
    attach( entity );
    return entity;
  }

  @Memoize
  public boolean isNotEmpty()
  {
    return 0 != totalCount();
  }

  @Memoize
  public int totalCount()
  {
    return (int) entities().count();
  }

  @Memoize
  int activeCount()
  {
    return (int) entities().filter( todo -> !todo.isCompleted() ).count();
  }

  @Memoize
  public int completedCount()
  {
    return totalCount() - activeCount();
  }

  @Override
  @Action( reportParameters = false )
  public void destroy( @Nonnull final Todo entity )
  {
    super.destroy( entity );
  }
}
