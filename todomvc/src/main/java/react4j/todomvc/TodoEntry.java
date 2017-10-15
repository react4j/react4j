package react4j.todomvc;

import elemental2.dom.HTMLInputElement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactElement;
import react4j.dom.events.FormEvent;
import react4j.dom.events.FormEventHandler;
import react4j.dom.events.KeyboardEvent;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.proptypes.html.InputProps;
import react4j.todomvc.model.AppData;
import static react4j.dom.DOM.*;
import static react4j.todomvc.TodoEntry_.*;

@ReactComponent
class TodoEntry
  extends ReactArezComponent<BaseProps, TodoEntry.State>
{
  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class State
    extends BaseState
  {
    String newTodo;

    @JsOverlay
    static TodoEntry.State create( @Nonnull final String newTodo )
    {
      final TodoEntry.State state = new TodoEntry.State();
      state.newTodo = newTodo;
      return state;
    }
  }

  @Override
  protected void componentDidConstruct()
  {
    setInitialState( TodoEntry.State.create( "" ) );
  }

  @EventHandler( KeyboardEventHandler.class )
  void handleNewTodoKeyDown( @Nonnull final KeyboardEvent event )
  {
    if ( KeyCodes.ENTER_KEY == event.keyCode )
    {
      event.preventDefault();
      final String val = state().newTodo.trim();
      if ( val.length() > 0 )
      {
        AppData.service.addTodo( val );
        setTodoText( "" );
      }
    }
  }

  @EventHandler( FormEventHandler.class )
  void handleChange( @Nonnull final FormEvent event )
  {
    final HTMLInputElement input = Js.cast( event.target );
    setTodoText( input.value );
  }

  private void setTodoText( @Nonnull final String value )
  {
    setState( State.create( value ) );
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> render()
  {
    return input( new InputProps()
                    .className( "new-todo" )
                    .placeHolder( "What needs to be done?" )
                    .value( state().newTodo )
                    .onKeyDown( _handleNewTodoKeyDown( this ) )
                    .onChange( _handleChange( this ) )
                    .autoFocus( true )
    );
  }
}
