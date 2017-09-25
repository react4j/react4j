package org.realityforge.react.todo_mvc.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.arez.Arez;
import org.realityforge.arez.Disposable;
import org.realityforge.arez.Observable;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.Container;

@SuppressWarnings( "ConstantConditions" )
@Container( singleton = true )
public class TodoRepository
{
  private final Observable _observable =
    Arez.context().createObservable( Arez.context().areNamesEnabled() ? "Todos.todo" : null );
  private final HashMap<Object, Todo> _entities = new HashMap<>();
  private final Collection<Todo> _entityList = Collections.unmodifiableCollection( _entities.values() );

  @Action
  public void create( @Nonnull final String id, @Nonnull final String title, final boolean completed )
  {
    final Arez_Todo entity = new Arez_Todo( id, title, completed );
    _entities.put( entity.getId(), entity );
    _observable.reportChanged();
  }

  @Action
  public boolean contains( @Nonnull final Todo todo )
  {
    return _entities.containsKey( todo.getId() );
  }

  @Action
  public void destroy( @Nonnull final Todo todo )
  {
    //TODO: Should this next line be an invariant check
    assert null != todo;
    if ( null != _entities.remove( todo.getId() ) )
    {
      Disposable.dispose( todo );
      //TODO: Remove this next line as should not be needed as downstream will trigger on deletion?
      // unless of course todo is not disposable
      _observable.reportChanged();
    }
    else
    {
      //TODO: fail an apiInvariant check?
    }
  }

  @Nullable
  public final Todo findById( @Nonnull final String id )
  {
    return _entities.get( id );
  }

  @Nonnull
  public final Collection<Todo> findAll()
  {
    _observable.reportObserved();
    return _entityList;
  }

  @Nonnull
  public final TodoRepository self()
  {
    return this;
  }
}
