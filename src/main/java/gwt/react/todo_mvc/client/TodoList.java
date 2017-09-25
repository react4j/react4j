package gwt.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import gwt.react.client.api.React;
import gwt.react.client.components.BaseProps;
import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.events.FormEvent;
import gwt.react.client.events.KeyboardEvent;
import gwt.react.client.proptypes.html.HtmlProps;
import gwt.react.client.proptypes.html.InputProps;
import gwt.react.client.proptypes.html.attributeTypes.InputType;
import gwt.react.client.util.Array;
import gwt.react.client.util.JsUtil;
import gwt.react.todo_mvc.client.model.Todo;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.Container;
import static gwt.react.client.api.GwtReact.castAsReactElement;
import static gwt.react.client.api.React.DOM.*;

/**
 * TODO: Should be generate an Arez subclass of this that generates all action wrappers.
 */
@Container
class TodoList
  extends ArezComponent<BaseProps, TodoList.State>
{
  static final String NOW_SHOWING_ACTIVE_TODOS = "active";
  static final String NOW_SHOWING_COMPLETED_TODOS = "completed";

  enum ActionType
  {
    EDIT, DESTROY, TOGGLE, CANCEL
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class State
    extends BaseState
  {
    String editingId;
    String newTodo;

    @JsOverlay
    public static State create( final String editingId, final String newTodo )
    {
      final State state = new State();
      state.editingId = editingId;
      state.newTodo = newTodo;
      return state;
    }
  }

  TodoList( @Nonnull final Component<BaseProps, State> component )
  {
    super( component );
    setInitialState( State.create( null, "" ) );
  }

  @Nonnull
  @Override
  protected String getTypeName()
  {
    return "TodoList";
  }

  private void handleDoAction( final ActionType actionType, final Todo todo )
  {
    switch ( actionType )
    {
      case TOGGLE:
        App.model.toggle( todo );
        break;
      case CANCEL:
        setEditingId( null );
        break;
      case DESTROY:
        App.model.destroy( todo );
        break;
      case EDIT:
        setEditingId( todo.getId() );
    }
  }

  private void handleSave( final Todo todoToSave, final String text )
  {
    App.model.save( todoToSave, text );
    final String editingId = null;
    setEditingId( editingId );
  }

  @Action
  void setEditingId( final String editingId )
  {
    setState( State.create( editingId, state().newTodo ) );
  }

  private void handleClearCompleted()
  {
    App.model.clearCompleted();
  }

  private void handleToggleAll( FormEvent event )
  {
    final HTMLInputElement input = Js.cast( event.target );
    App.model.toggleAll( input.checked );
  }

  private void handleNewTodoKeyDown( @Nonnull final KeyboardEvent event )
  {
    if ( App.ENTER_KEY == event.keyCode )
    {
      event.preventDefault();
      addNewTodo();
    }
  }

  @Action
  void addNewTodo()
  {
    final String val = state().newTodo.trim();
    if ( val.length() > 0 )
    {
      App.model.addTodo( val );
      setTodoText( "" );
    }
  }

  private void handleChange( @Nonnull final FormEvent event )
  {
    final HTMLInputElement input = Js.cast( event.target );
    setTodoText( input.value );
  }

  @Action
  void setTodoText( @Nonnull final String value )
  {
    setState( State.create( state().editingId, value ) );
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    final Array<Todo> todos = App.model.todos;
    final int todoCount = todos.getLength();

    final int activeTodoCount =
      todos.reduce( ( accum, currentValue, index, theArray ) -> currentValue.isCompleted() ? accum : accum + 1, 0 );
    final int completedCount = todoCount - activeTodoCount;
    return
      div( null,
           div( null,
                header( new HtmlProps().className( "header" ),
                        h1( null, "todos" ),
                        input( new InputProps()
                                 .className( "new-todo" )
                                 .placeHolder( "What needs to be done?" )
                                 .value( state().newTodo )
                                 .onKeyDown( this::handleNewTodoKeyDown )
                                 .onChange( this::handleChange )
                                 .autoFocus( true )
                        )
                ),
                renderMainSection(),
                renderFooter( activeTodoCount, completedCount )
           )
      );
  }

  @Nullable
  private ReactElement<?, ?> renderMainSection()
  {
    final Array<Todo> todos = App.model.todos;
    final int todoCount = todos.getLength();
    if ( todoCount > 0 )
    {
      final Array<Todo> shownTodos = findShownTodos( todos );
      return section( new HtmlProps().className( "header" ),
                      input( new InputProps()
                               .className( "toggle-all" )
                               .type( InputType.checkbox )
                               .onChange( this::handleToggleAll )
                      ),
                      ul( new HtmlProps()
                            .className( "todo-list" ),
                          castAsReactElement( renderTodoItems( shownTodos ) )
                      )
      );
    }
    else
    {
      return null;
    }
  }

  private Array<Todo> findShownTodos( final Array<Todo> todos )
  {
    final String nowShowing = getNowShowing();
    return todos.filter( ( todo, index, theArray ) -> {
      if ( nowShowing.isEmpty() )
      {
        return true;
      }
      else if ( nowShowing.equals( NOW_SHOWING_ACTIVE_TODOS ) )
      {
        return !todo.isCompleted();
      }
      else
      {
        return todo.isCompleted();
      }
    } );
  }

  @Nonnull
  private String getNowShowing()
  {
    return TestData.LOCATION.getLocation();
  }

  private Array<ReactElement<?, ?>> renderTodoItems( final Array<Todo> shownTodos )
  {
    return shownTodos.map( ( todo, index, theArray ) -> {
      final boolean isEditing = Objects.equals( state().editingId, todo.getId() );
      final TodoItem.Props props = TodoItem.Props.create( todo, this::handleSave, this::handleDoAction, isEditing );
      return React.createElement( TodoItem_.TYPE, props );
    } );
  }

  @Nullable
  private ReactElement<?, ?> renderFooter( final int activeTodoCount, final int completedCount )
  {
    if ( activeTodoCount > 0 || completedCount > 0 )
    {
      final Footer.Props props =
        Footer.Props.create( activeTodoCount,
                             completedCount,
                             getNowShowing(),
                             e -> handleClearCompleted() );
      JsUtil.definePropertyValue( props.onClearCompleted, "name", "handleClearCompleted" );
      return React.createElement( Footer_.TYPE, props );
    }
    else
    {
      return null;
    }
  }
}
