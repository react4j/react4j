package gwt.react.todo_mvc.client;

import gwt.interop.utils.client.collections.JsArray;
import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.functional.JsProcedure;
import java.util.Date;

class TodoModel
{
  // Generic "model" object. You can use whatever
  // framework you want. For this application it
  // may not even be worth separating this logic
  // out, but we do this to demonstrate one way to
  // separate out parts of your application.
  static class Todo
  {
    String id;
    String title;
    boolean completed;

    Todo( String id, String title, boolean completed )
    {
      this.id = id;
      this.title = title;
      this.completed = completed;
    }
  }

  Array<Todo> todos = JsArray.create();
  private Array<JsProcedure> onChanges = JsArray.create();

  void subscribe( JsProcedure onChange )
  {
    onChanges.push( onChange );
  }

  private void inform()
  {
    //Utils.store(this.key, this.todos);
    onChanges.forEachElem( ( v, index, theArray ) -> v.call() );
  }

  void addTodo( String title )
  {
    todos = todos.concatValue( new Todo( Long.toString( new Date().getTime() ), title, false ) );

    inform();

  }

  void toggleAll( boolean checked )
  {
    // Note: it's usually better to use immutable data structures since they're
    // easier to reason about and React works very well with them. That's why
    // we use map() and filter() everywhere instead of mutating the array or
    // todo items themselves.

    todos = todos.map( ( v, index, theArray ) -> new Todo( v.id, v.title, checked ) );

    inform();
  }

  void toggle( Todo todoToToggle )
  {
    todos = todos.map( ( v, index, theArray ) -> ( v == todoToToggle ) ? new Todo( v.id, v.title, !v.completed ) : v );

    inform();
  }

  void destroy( Todo todo )
  {
    todos = todos.filter( ( v, index, theArray ) -> ( v != todo ) );

    inform();
  }

  void save( Todo todoToSave, String newTitle )
  {
    todos = todos.map( ( v, index, theArray ) -> ( v == todoToSave ) ? new Todo( v.id, newTitle, v.completed ) : v );

    inform();
  }

  void clearCompleted()
  {
    todos = todos.filter( ( v, index, theArray ) -> !v.completed );

    inform();
  }
}
