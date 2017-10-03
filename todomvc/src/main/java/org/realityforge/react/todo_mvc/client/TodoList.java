package org.realityforge.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
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
  extends ReactArezComponent<BaseProps, BaseState>
{
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
    return
      div(
        div( header( new HtmlProps().className( "header" ),
                     h1( "todos" ),
                     React.createElement( TodoEntry_.TYPE )
             ),
             renderMainSection(),
             renderFooter()
        )
      );
  }

  @Nullable
  private ReactElement<?, ?> renderMainSection()
  {
    if ( AppData.model.totalCount() > 0 )
    {
      final FilterMode filterMode = AppData.viewService.getFilterMode();
      final List<Todo> shownTodos =
        AppData.model.findAll().stream().
          filter( todo -> shouldShowTodo( filterMode, todo ) ).
          collect( Collectors.toList() );
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
    return React.createElement( TodoItem_.TYPE, TodoItem.Props.create( todo ) );
  }

  @Nullable
  private ReactElement<?, ?> renderFooter()
  {
    if ( AppData.model.activeCount() > 0 || AppData.model.completedCount() > 0 )
    {
      return React.createElement( Footer_.TYPE );
    }
    else
    {
      return null;
    }
  }
}
