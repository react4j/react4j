package org.realityforge.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.react.todo_mvc.client.model.AppData;
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
    if ( AppData.model.isNotEmpty() )
    {
      return section( new HtmlProps().className( "header" ),
                      input( new InputProps()
                               .className( "toggle-all" )
                               .type( InputType.checkbox )
                               .onChange( _handleToggleAll( this ) )
                      ),
                      ul( new HtmlProps()
                            .className( "todo-list" ),
                          renderTodoItems()
                      )
      );
    }
    else
    {
      return null;
    }
  }

  private List<ReactElement<?, ?>> renderTodoItems()
  {
    return AppData.model.filteredTodos().stream().
      map( todo -> React.createElement( TodoItem_.TYPE, TodoItem.Props.create( todo ) ) ).
      collect( Collectors.toList() );
  }

  @Nullable
  private ReactElement<?, ?> renderFooter()
  {
    if ( AppData.model.isNotEmpty() )
    {
      return React.createElement( Footer_.TYPE );
    }
    else
    {
      return null;
    }
  }
}
