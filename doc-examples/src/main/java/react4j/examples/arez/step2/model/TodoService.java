package react4j.examples.arez.step2.model;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Singleton;

@Singleton
@ArezComponent
public class TodoService
{
  @Nonnull
  private final TodoRepository _repository;

  TodoService( @Nonnull final TodoRepository repository )
  {
    _repository = Objects.requireNonNull( repository );
  }

  @Action
  public void save( final Todo todo, final String newTitle )
  {
    todo.setTitle( newTitle );
  }

  @Action
  public void clearCompleted()
  {
    _repository.findAll().stream()
      //Find all completed
      .filter( Todo::isCompleted )
      .collect( Collectors.toList() )
      // Have to collect() to create new list so can mutate
      // by destroy in following line
      .forEach( _repository::destroy );
  }
}
