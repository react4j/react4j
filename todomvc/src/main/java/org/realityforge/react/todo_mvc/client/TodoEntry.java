package org.realityforge.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.react.todo_mvc.client.model.AppData;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.arez.ReactArezComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;
import react.dom.events.FormEvent;
import react.dom.events.FormEventHandler;
import react.dom.events.KeyboardEvent;
import react.dom.events.KeyboardEventHandler;
import react.dom.proptypes.html.InputProps;
import static org.realityforge.react.todo_mvc.client.TodoEntry_.*;
import static react.dom.DOM.*;

@ReactComponent
@ArezComponent
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
  protected void componentInitialize()
  {
    setInitialState( TodoEntry.State.create( "" ) );
  }

  @EventHandler( KeyboardEventHandler.class )
  void handleNewTodoKeyDown( @Nonnull final KeyboardEvent event )
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

  @EventHandler( FormEventHandler.class )
  void handleChange( @Nonnull final FormEvent event )
  {
    final HTMLInputElement input = Js.cast( event.target );
    setTodoText( input.value );
  }

  @Action
  void setTodoText( @Nonnull final String value )
  {
    setState( State.create( value ) );
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
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
