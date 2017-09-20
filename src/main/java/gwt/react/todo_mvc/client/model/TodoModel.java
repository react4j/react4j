package gwt.react.todo_mvc.client.model;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.JsArrayHelper;
import gwt.interop.utils.shared.functional.JsProcedure;
import java.util.Date;

public class TodoModel
{
  public Array<Todo> todos = JsArrayHelper.createArray();
  private Array<JsProcedure> onChanges = JsArrayHelper.createArray();

  public void subscribe( JsProcedure onChange )
  {
    onChanges.push( onChange );
  }

  private void inform()
  {
    onChanges.forEachElem( ( v, index, theArray ) -> v.call() );
  }

  public void addTodo( String title )
  {
    todos = todos.concatValue( new Todo( Long.toString( new Date().getTime() ), title, false ) );
    inform();
  }

  public void toggleAll( boolean checked )
  {
    // Note: it's usually better to use immutable data structures since they're
    // easier to reason about and React works very well with them. That's why
    // we use map() and filter() everywhere instead of mutating the array or
    // todo items themselves.

    todos = todos.map( ( v, index, theArray ) -> new Todo( v.getId(), v.getTitle(), checked ) );

    inform();
  }

  public void toggle( Todo todoToToggle )
  {
    todos = todos.map( ( v, index, theArray ) -> ( v == todoToToggle ) ?
                                                 new Todo( v.getId(),
                                                           v.getTitle(), !v.isCompleted() ) :
                                                 v );

    inform();
  }

  public void destroy( Todo todo )
  {
    todos = todos.filter( ( v, index, theArray ) -> ( v != todo ) );

    inform();
  }

  public void save( Todo todoToSave, String newTitle )
  {
    todos = todos.map( ( v, index, theArray ) -> ( v == todoToSave ) ? new Todo( v.getId(), newTitle,
                                                                                 v.isCompleted() ) : v );

    inform();
  }

  public void clearCompleted()
  {
    todos = todos.filter( ( v, index, theArray ) -> !v.isCompleted() );

    inform();
  }
}
