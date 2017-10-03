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
import org.realityforge.react.todo_mvc.client.model.AppData;
import org.realityforge.react.todo_mvc.client.model.FilterMode;
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
  enum ActionType
  {
    EDIT, DESTROY, TOGGLE, CANCEL
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class State
    extends BaseState
  {
    // TODO: Move this state to view store of some sort so changing editingId does not redraw this entire TodoList component
    // then have each TodoItem have a @computed property that controls whether they redraw?
    String editingId;

    @JsOverlay
    static State create( @Nullable final String editingId )
    {
      final State state = new State();
      state.editingId = editingId;
      return state;
    }
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
    setState( State.create( editingId ) );
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
      final FilterMode filterMode = AppData.viewService.getFilterMode();
      final List<Todo> shownTodos =
        todos.stream().filter( todo -> shouldShowTodo( filterMode, todo ) ).collect( Collectors.toList() );
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

  private boolean shouldShowTodo( @Nonnull final FilterMode filterMode, @Nonnull final Todo todo )
  {
    if ( FilterMode.ALL == filterMode )
    {
      return true;
    }
    else if ( FilterMode.ACTIVE == filterMode )
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
    final TodoItem.Props props = TodoItem.Props.create( todo, _handleSave( this ), _handleDoAction( this ), isEditing );
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
                             AppData.viewService.getFilterMode(),
                             _handleClearCompleted( this ) );
      return React.createElement( Footer_.TYPE, props );
    }
    else
    {
      return null;
    }
  }
}
