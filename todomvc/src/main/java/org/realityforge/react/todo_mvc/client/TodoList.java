package org.realityforge.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import java.util.Collection;
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
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.Autorun;
import org.realityforge.react.todo_mvc.client.model.AppData;
import org.realityforge.react.todo_mvc.client.model.Todo;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.arez.ReactArezComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.React;
import react.core.ReactElement;
import react.dom.events.FormEvent;
import react.dom.events.FormEventHandler;
import react.dom.events.MouseEventHandler;
import react.dom.proptypes.html.HtmlProps;
import react.dom.proptypes.html.InputProps;
import react.dom.proptypes.html.attributeTypes.InputType;
import static org.realityforge.react.todo_mvc.client.TodoList_.*;
import static react.dom.DOM.*;

@ReactComponent
@ArezComponent
class TodoList
  extends ReactArezComponent<BaseProps, TodoList.State>
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
    String nowShowing;

    @JsOverlay
    static State create( final String editingId, final String nowShowing )
    {
      final State state = new State();
      state.editingId = editingId;
      state.nowShowing = nowShowing;
      return state;
    }
  }

  @Override
  protected void componentInitialize()
  {
    setInitialState( () -> State.create( null, AppData.LOCATION.getLocation() ) );
  }

  @Autorun
  void updateNowShowing()
  {
    // Observe the state here so we will re-run if it changes
    // regardless of whether component is bound yet
    final String location = AppData.LOCATION.getLocation();

    if ( isComponentBound() )
    {
      // Deliberately avoid observing state as it should only
      // be run when location changes
      final State state = component().state().dup();
      state.nowShowing = location;
      setState( state );
    }
    App.whyRun();
  }

  @EventHandler( TodoItem.OnAction.class )
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

  @EventHandler( TodoItem.OnSave.class )
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
    setState( State.create( editingId, state.nowShowing ) );
  }

  @EventHandler( MouseEventHandler.class )
  void handleClearCompleted()
  {
    AppData.service.clearCompleted();
  }

  @EventHandler( FormEventHandler.class )
  void handleToggleAll( FormEvent event )
  {
    final HTMLInputElement input = Js.cast( event.target );
    AppData.service.toggleAll( input.checked );
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    final Collection<Todo> todos = AppData.model.findAll();
    final int todoCount = todos.size();

    final int activeTodoCount = (int) todos.stream().filter( todo -> !todo.isCompleted() ).count();
    final int completedCount = todoCount - activeTodoCount;
    return
      div(
        div( header( new HtmlProps().className( "header" ),
                     h1( "todos" ),
                     React.createElement( TodoEntry_.TYPE, new BaseProps() )
             ),
             renderMainSection(),
             renderFooter( activeTodoCount, completedCount )
        )
      );
  }

  @Nullable
  private ReactElement<?, ?> renderMainSection()
  {
    final Collection<Todo> todos = AppData.model.findAll();
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
                               .onChange( _handleToggleAll( this ) )
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
                             _handleClearCompleted( this ) );
      return React.createElement( Footer_.TYPE, props );
    }
    else
    {
      return null;
    }
  }
}
