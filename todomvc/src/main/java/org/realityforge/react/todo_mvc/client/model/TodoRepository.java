package org.realityforge.react.todo_mvc.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.arez.Arez;
import org.realityforge.arez.Disposable;
import org.realityforge.arez.Observable;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;

/**
 * TODO: This class should be entirely generated.
 * Perhaps with additional query capabilities sourced out of replicant.
 */
@SuppressWarnings( "ConstantConditions" )
@ArezComponent( singleton = true )
@Generated( "Arez" )
public class TodoRepository
  implements TodoRepositoryExtension
{
  private final Observable _observable =
    Arez.context().createObservable( Arez.context().areNamesEnabled() ? "Todos.todo" : null );
  private final HashMap<Object, Todo> _entities = new HashMap<>();
  private final Collection<Todo> _entityList = Collections.unmodifiableCollection( _entities.values() );

  @Nonnull
  public static TodoRepository create()
  {
    return new Arez_TodoRepository();
  }

  TodoRepository()
  {
  }

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
  public final Todo getById( @Nonnull final String id )
  {
    final Todo todo = findById( id );
    if( null == todo )
    {
      throw new NoSuchEntityException( "Todo", id );
    }
    return todo;
  }

  @Nonnull
  public final Collection<Todo> findAll()
  {
    _observable.reportObserved();
    return _entityList;
  }

  @Nonnull
  public final List<Todo> findAll( @Nonnull final Comparator<Todo> sorter )
  {
    return findAll().stream().sorted( sorter ).collect( Collectors.toList() );
  }

  @Nonnull
  public final List<Todo> findAllByQuery( @Nonnull final Predicate<Todo> query )
  {
    return findAll().stream().filter( query ).collect( Collectors.toList() );
  }

  @Nonnull
  public final List<Todo> findAllByQuery( @Nonnull final Predicate<Todo> query,
                                          @Nonnull final Comparator<Todo> sorter )
  {
    return findAll().stream().filter( query ).sorted( sorter ).collect( Collectors.toList() );
  }

  //TODO: Should we support a getById and getByQuery that throws exceptions if unable to find element?

  /**
   * Find an entity based on specified query, returning null if not present.
   *
   * @param query the query.
   * @return the entity or null if no such entity.
   */
  @Nullable
  public final Todo findByQuery( @Nonnull final Predicate<Todo> query )
  {
    return findAll().stream().filter( query ).findFirst().orElse( null );
  }

  /**
   * Return an entity based on specified query, raising a NoResultException if no matching entity.
   *
   * @param query the query.
   * @return the entity.
   */
  @Nonnull
  public final Todo getByQuery( @Nonnull final Predicate<Todo> query )
    throws NoResultException
  {
    final Todo result = findByQuery( query );
    if ( null == result )
    {
      throw new NoResultException();
    }
    return result;
  }

  @Override
  @Nonnull
  public final TodoRepository self()
  {
    return this;
  }
}
