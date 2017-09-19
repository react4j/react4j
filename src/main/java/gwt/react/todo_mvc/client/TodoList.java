package gwt.react.todo_mvc.client;

import com.google.gwt.dom.client.InputElement;
import gwt.interop.utils.client.plainobjects.JsPlainObj;
import gwt.interop.utils.shared.collections.Array;
import gwt.react.client.api.React;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.events.FormEvent;
import gwt.react.client.events.KeyboardEvent;
import gwt.react.client.events.MouseEvent;
import gwt.react.client.proptypes.BaseProps;
import gwt.react.client.proptypes.html.HtmlProps;
import gwt.react.client.proptypes.html.InputProps;
import gwt.react.client.proptypes.html.attributeTypes.InputType;
import gwt.react_router.client.HistoryLocation;
import gwt.react_router.client.RouterEnhancedProps;
import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jetbrains.annotations.Nullable;
import static gwt.interop.utils.client.plainobjects.JsPlainObj.$;
import static gwt.react.client.api.GwtReact.castAsReactElement;
import static gwt.react.client.api.React.DOM.*;

@JsType
class TodoList
  extends Component<TodoList.TodoListProps, TodoList.TodoListState>
{
  static final String NOW_SHOWING_ACTIVE_TODOS = "active";
  static final String NOW_SHOWING_COMPLETED_TODOS = "completed";

  enum Action
  {
    EDIT, DESTROY, TOGGLE, CANCEL
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class TodoRouterParams
  {
    String nowShowing;
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class TodoListProps
    extends BaseProps
    implements RouterEnhancedProps<TodoRouterParams>
  {

    //Having to define the JsProperty annotations again when implementing an interface
    //has been logged as an issue that should be fixed in the future
    @JsProperty( name = "location" )
    public native HistoryLocation getRouterLocation();

    @JsProperty( name = "params" )
    public native TodoRouterParams getRouterParams();
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class TodoListState
    extends JsPlainObj
  {
    String editingId;
    String newTodo;
  }

  public TodoList( TodoList.TodoListProps props )
  {
    super( props );
    state = $( new TodoListState(), "editingId", null, "newTodo", "" );
  }

  private void handleDoAction( final Action action, final TodoModel.Todo todo )
  {
    switch ( action )
    {
      case TOGGLE:
        App.model.toggle( todo );
        break;
      case CANCEL:
        setState( $( new TodoListState(), "editingId", null ) );
        break;
      case DESTROY:
        App.model.destroy( todo );
        break;
      case EDIT:
        setState( $( new TodoListState(), "editingId", todo.id ) );
    }
  }

  private void handleSave( final TodoModel.Todo todoToSave, final String text )
  {
    App.model.save( todoToSave, text );
    setState( $( new TodoListState(), "editingId", null ) );
  }

  private void handleClearCompleted( MouseEvent event )
  {
    App.model.clearCompleted();
  }

  private void handleToggleAll( FormEvent event )
  {
    App.model.toggleAll( InputElement.as( event.target ).isChecked() );
  }

  private void handleNewTodoKeyDown( @Nonnull final KeyboardEvent event )
  {
    if ( App.ENTER_KEY == event.keyCode )
    {
      event.preventDefault();

      final String val = state.newTodo.trim();

      if ( val.length() > 0 )
      {
        App.model.addTodo( val );
        setState( $( new TodoListState(), "newTodo", "" ) );
      }
    }
  }

  private void handleChange( FormEvent event )
  {
    setState( $( new TodoListState(), "newTodo", InputElement.as( event.target ).getValue() ) );
  }

  public ReactElement<?, ?> render()
  {
    final Array<TodoModel.Todo> todos = App.model.todos;
    final int todoCount = todos.getLength();

    final int activeTodoCount =
      todos.reduce( ( accum, currentValue, index, theArray ) -> currentValue.completed ? accum : accum + 1, 0 );
    final int completedCount = todoCount - activeTodoCount;
    return
      div( null,
           header( new HtmlProps().className( "header" ),
                   h1( null, "todos" ),
                   input( new InputProps()
                            .className( "new-todo" )
                            .placeHolder( "What needs to be done?" )
                            .value( state.newTodo )
                            .onKeyDown( this::handleNewTodoKeyDown )
                            .onChange( this::handleChange )
                            .autoFocus( true ) ) ),
           renderMainSection(),
           renderFooter( activeTodoCount, completedCount )
      );
  }

  @Nullable
  private ReactElement<?, ?> renderMainSection()
  {
    final String nowShowing = props.getRouterParams().nowShowing;
    final Array<TodoModel.Todo> todos = App.model.todos;
    final int todoCount = todos.getLength();
    ReactElement<?, ?> main = null;
    if ( todoCount > 0 )
    {
      final Array<TodoModel.Todo> shownTodos = findShownTodos( todos, nowShowing );
      main = section( new HtmlProps().className( "header" ),
                      input( new InputProps()
                               .className( "toggle-all" )
                               .type( InputType.checkbox )
                               .onChange( this::handleToggleAll ) ),
                      ul( new HtmlProps()
                            .className( "todo-list" ),
                          castAsReactElement( renderTodoItems( shownTodos ) )
                      )
      );
    }
    return main;
  }

  private Array<TodoModel.Todo> findShownTodos( final Array<TodoModel.Todo> todos, final String nowShowing )
  {
    return todos.filter( ( todo, index, theArray ) -> {
      if ( nowShowing == null )
      {
        return true;
      }
      else if ( nowShowing.equals( NOW_SHOWING_ACTIVE_TODOS ) )
      {
        return !todo.completed;
      }
      else
      {
        return todo.completed;
      }
    } );
  }

  private Array<ReactElement<?, ?>> renderTodoItems( final Array<TodoModel.Todo> shownTodos )
  {
    return shownTodos.map( ( todo, index, theArray ) -> {
      final TodoItem.TodoItemProps todoProps = new TodoItem.TodoItemProps();

      todoProps.key = todo.id;
      todoProps.todo = todo;
      todoProps.doAction = this::handleDoAction;
      todoProps.doSave = this::handleSave;
      todoProps.isEditing = Objects.equals( state.editingId, todo.id );

      return React.createElement( TodoItem.class, todoProps );
    } );
  }

  @Nullable
  private ReactElement<?, ?> renderFooter( final int activeTodoCount, final int completedCount )
  {
    final ReactElement<?, ?> footer;
    if ( activeTodoCount > 0 || completedCount > 0 )
    {
      final Footer.FooterProps footerProps = new Footer.FooterProps();
      footerProps.count = activeTodoCount;
      footerProps.completedCount = completedCount;
      footerProps.nowShowing = props.getRouterParams().nowShowing;
      footerProps.onClearCompleted = this::handleClearCompleted;

      footer = React.createElement( Footer.component, footerProps );
    }
    else
    {
      footer = null;
    }
    return footer;
  }
}
