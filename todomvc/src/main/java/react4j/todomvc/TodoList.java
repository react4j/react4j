package react4j.todomvc;

import elemental2.dom.HTMLInputElement;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.React;
import react4j.core.ReactElement;
import react4j.dom.events.FormEvent;
import react4j.dom.events.FormEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.todomvc.model.AppData;
import static react4j.dom.DOM.*;
import static react4j.todomvc.TodoList_.*;

@ReactComponent
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
  protected ReactElement<?, ?> renderAsElement()
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
    return AppData.model.isNotEmpty() ? React.createElement( Footer_.TYPE ) : null;
  }
}
