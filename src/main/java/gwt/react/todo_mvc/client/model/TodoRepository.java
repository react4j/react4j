package gwt.react.todo_mvc.client.model;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import org.realityforge.arez.Arez;
import org.realityforge.arez.Disposable;
import org.realityforge.arez.Observable;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.Container;

@Container( singleton = true )
public class TodoRepository
{
  private final Observable _todosObservable =
    Arez.context().createObservable( Arez.context().areNamesEnabled() ? "Todos.todo" : null );
  private final ArrayList<Todo> todos = new ArrayList<>();

  @Action
  public void create( @Nonnull final String id, @Nonnull final String title, final boolean completed )
  {
    todos.add( new Arez_Todo( id, title, completed ) );
    _todosObservable.reportChanged();
  }

  @Action
  public void destroy( final Todo todo )
  {
    if ( todos.remove( todo ) )
    {
      Disposable.dispose( todo );
      //TODO: Remove this next line as should not be needed as downstream will trigger on deletion?
      // unless of course todo is not disposable
      _todosObservable.reportChanged();
    }
  }

  public ArrayList<Todo> findAll()
  {
    _todosObservable.reportObserved();
    return todos;
  }
}
