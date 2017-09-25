package gwt.react.todo_mvc.client;

import elemental2.dom.DomGlobal;
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
import gwt.react.client.util.JsUtil;
import gwt.react.todo_mvc.client.model.AppData;
import gwt.react.todo_mvc.client.model.Todo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.Autorun;
import org.realityforge.arez.annotations.Container;
import static gwt.react.client.api.React.DOM.*;

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
    String nowShowing;

    @JsOverlay
    public static State create( final String editingId, final String newTodo, final String nowShowing )
    {
      final State state = new State();
      state.editingId = editingId;
      state.newTodo = newTodo;
      state.nowShowing = nowShowing;
      return state;
    }
  }

  TodoList( @Nonnull final Component<BaseProps, State> component )
  {
    super( component );
    setInitialState( () -> State.create( null, "", AppData.LOCATION.getLocation() ) );
  }

  //@PostConstruct
  //void postConstruct()
  //{
  //  updateNowShowing();
  //}
  //
  @Autorun
  void updateNowShowing()
  {
    // Deliberately avoid observing state as it should only
    // be run when location changes
    final State state = component().state().dup();
    state.nowShowing = AppData.LOCATION.getLocation();
    setState( state );
    App.whyRun();
  }

  @Nonnull
  @Override
  protected String getTypeName()
  {
    return "TodoList";
  }

  @Action
  void handleDoAction( final ActionType actionType, final Todo todo )
  {
    switch ( actionType )
    {
      case TOGGLE:
        todo.toggle();
        break;
      case CANCEL:
        setEditingId( null );
        break;
      case DESTROY:
        AppData.model.destroy( todo );
        break;
      case EDIT:
        setEditingId( todo.getId() );
    }
  }

  @Action
  void handleSave( final Todo todoToSave, final String text )
  {
    AppData.service.save( todoToSave, text );
    setEditingId( null );
  }

  @Action
  void setEditingId( @Nullable final String editingId )
  {
    final State state = state();
    setState( State.create( editingId, state.newTodo, state.nowShowing ) );
  }

  private void handleClearCompleted()
  {
    AppData.service.clearCompleted();
  }

  private void handleToggleAll( FormEvent event )
  {
    final HTMLInputElement input = Js.cast( event.target );
    AppData.service.toggleAll( input.checked );
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
      AppData.service.addTodo( val );
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
    setState( State.create( state().editingId, value, state().nowShowing ) );
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    final ArrayList<Todo> todos = AppData.model.findAll();
    final int todoCount = todos.size();

    final int activeTodoCount = (int) todos.stream().filter( todo -> !todo.isCompleted() ).count();
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
    final ArrayList<Todo> todos = AppData.model.findAll();
    final int todoCount = todos.size();
    if ( todoCount > 0 )
    {
      final String nowShowing = state().nowShowing;
      final List<Todo> shownTodos =
        todos.stream().filter( todo -> shouldShowTodo( nowShowing, todo ) ).collect( Collectors.toList() );
      return section( new HtmlProps().className( "header" ),
                      input( new InputProps()
                               .className( "toggle-all" )
                               .type( InputType.checkbox )
                               .onChange( this::handleToggleAll )
                      ),
                      ul( new HtmlProps()
                            .className( "todo-list" ),
                          renderTodoItems( shownTodos )
                      )
      );
    }
    else
    {
      return null;
    }
  }

  private boolean shouldShowTodo( @Nullable final String currentCategory, @Nonnull final Todo todo )
  {
    if ( null == currentCategory || currentCategory.isEmpty() )
    {
      return true;
    }
    else if ( currentCategory.equals( NOW_SHOWING_ACTIVE_TODOS ) )
    {
      return !todo.isCompleted();
    }
    else
    {
      return todo.isCompleted();
    }
  }

  private List<ReactElement<?, ?>> renderTodoItems( final List<Todo> shownTodos )
  {
    return shownTodos.stream().map( this::renderTodo ).collect( Collectors.toList() );
  }

  @Nonnull
  private ReactElement<?, ?> renderTodo( @Nonnull final Todo todo )
  {
    final boolean isEditing = Objects.equals( state().editingId, todo.getId() );
    final TodoItem.Props props = TodoItem.Props.create( todo, this::handleSave, this::handleDoAction, isEditing );
    return React.createElement( TodoItem_.TYPE, props );
  }

  @Nullable
  private ReactElement<?, ?> renderFooter( final int activeTodoCount, final int completedCount )
  {
    if ( activeTodoCount > 0 || completedCount > 0 )
    {
      final Footer.Props props =
        Footer.Props.create( activeTodoCount,
                             completedCount,
                             state().nowShowing,
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
