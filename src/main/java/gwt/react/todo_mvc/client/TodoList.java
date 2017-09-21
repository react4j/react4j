package gwt.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import gwt.react.client.api.React;
import gwt.react.client.components.BaseProps;
import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.components.SideComponent;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.events.FormEvent;
import gwt.react.client.events.KeyboardEvent;
import gwt.react.client.proptypes.html.HtmlProps;
import gwt.react.client.proptypes.html.InputProps;
import gwt.react.client.proptypes.html.attributeTypes.InputType;
import gwt.react.client.util.Array;
import gwt.react.client.util.JsUtil;
import gwt.react.todo_mvc.client.model.Todo;
import gwt.react_router.client.HistoryLocation;
import gwt.react_router.client.RouterEnhancedProps;
import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;
import org.jetbrains.annotations.Nullable;
import static gwt.react.client.api.GwtReact.castAsReactElement;
import static gwt.react.client.api.React.DOM.*;

class TodoList
  extends SideComponent<TodoList.Props, TodoList.State>
{
  static final JsConstructorFn<TodoListWrapper> TYPE = TodoListWrapper.ctor();

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
  static class Props
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

  TodoList( @Nonnull final Component<TodoList.Props, TodoList.State> component )
  {
    super( component );
    setInitialState( State.create( null, "" ) );
  }

  private void handleDoAction( final Action action, final Todo todo )
  {
    switch ( action )
    {
      case TOGGLE:
        App.model.toggle( todo );
        break;
      case CANCEL:
        setState( State.create( null, state().newTodo ) );
        break;
      case DESTROY:
        App.model.destroy( todo );
        break;
      case EDIT:
        setState( State.create( todo.getId(), state().newTodo ) );
    }
  }

  private void handleSave( final Todo todoToSave, final String text )
  {
    App.model.save( todoToSave, text );
    setState( State.create( null, state().newTodo ) );
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

      final String val = state().newTodo.trim();

      if ( val.length() > 0 )
      {
        App.model.addTodo( val );
        setState( State.create( state().editingId, "" ) );
      }
    }
  }

  private void handleChange( FormEvent event )
  {
    final HTMLInputElement input = Js.cast( event.target );
    setState( State.create( state().editingId, input.value ) );
  }

  @Override
  public ReactElement<?, ?> render()
  {
    final Array<Todo> todos = App.model.todos;
    final int todoCount = todos.getLength();

    final int activeTodoCount =
      todos.reduce( ( accum, currentValue, index, theArray ) -> currentValue.isCompleted() ? accum : accum + 1, 0 );
    final int completedCount = todoCount - activeTodoCount;
    return
      div( null,
           header( new HtmlProps().className( "header" ),
                   h1( null, "todos" ),
                   input( new InputProps()
                            .className( "new-todo" )
                            .placeHolder( "What needs to be done?" )
                            .value( state().newTodo )
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
    final String nowShowing = props().getRouterParams().nowShowing;
    final Array<Todo> todos = App.model.todos;
    final int todoCount = todos.getLength();
    ReactElement<?, ?> main = null;
    if ( todoCount > 0 )
    {
      final Array<Todo> shownTodos = findShownTodos( todos, nowShowing );
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

  private Array<Todo> findShownTodos( final Array<Todo> todos, final String nowShowing )
  {
    return todos.filter( ( todo, index, theArray ) -> {
      if ( nowShowing == null )
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

  private Array<ReactElement<?, ?>> renderTodoItems( final Array<Todo> shownTodos )
  {
    return shownTodos.map( ( todo, index, theArray ) -> {
      final TodoItem.Props todoProps = new TodoItem.Props();

      todoProps.key = todo.getId();
      todoProps.todo = todo;
      todoProps.doAction = this::handleDoAction;
      todoProps.doSave = this::handleSave;
      todoProps.isEditing = Objects.equals( state().editingId, todo.getId() );

      return React.createElement( TodoItem.TYPE, todoProps );
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
      footerProps.nowShowing = props().getRouterParams().nowShowing;
      footerProps.onClearCompleted = e -> handleClearCompleted();
      JsUtil.definePropertyValue( footerProps.onClearCompleted, "name", "handleClearCompleted" );

      footer = React.createElement( Footer.component, footerProps );
    }
    else
    {
      footer = null;
    }
    return footer;
  }
}
