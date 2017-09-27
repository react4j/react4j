package org.realityforge.react.todo_mvc.client.model;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;

@ArezComponent( singleton = true )
public class TodoService
{
  @Nonnull
  private final TodoRepository _repository;

  TodoService( @Nonnull final TodoRepository repository )
  {
    _repository = Objects.requireNonNull( repository );
  }

  @Action
  public void addTodo( @Nonnull final String title )
  {
    _repository.create( Long.toString( new Date().getTime() ), title, false );
  }

  @Action
  public void toggleAll( final boolean completed )
  {
    _repository.findAll().forEach( todo -> todo.setCompleted( completed ) );
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
