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
import org.realityforge.arez.annotations.Computed;
import org.realityforge.react.todo_mvc.client.model.AppData;
import org.realityforge.react.todo_mvc.client.model.Todo;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.arez.ReactArezComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;
import react.dom.events.FocusEventHandler;
import react.dom.events.FormEvent;
import react.dom.events.FormEventHandler;
import react.dom.events.KeyboardEvent;
import react.dom.events.KeyboardEventHandler;
import react.dom.events.MouseEventHandler;
import react.dom.proptypes.html.BtnProps;
import react.dom.proptypes.html.HtmlProps;
import react.dom.proptypes.html.InputProps;
import react.dom.proptypes.html.LabelProps;
import react.dom.proptypes.html.attributeTypes.InputType;
import static org.realityforge.react.todo_mvc.client.TodoItem_.*;
import static react.dom.DOM.*;

@ReactComponent
@ArezComponent
class TodoItem
  extends ReactArezComponent<TodoItem.Props, TodoItem.State>
{
  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  public static class Props
    extends BaseProps
  {
    Todo todo;

    @JsOverlay
    static Props create( @Nonnull final Todo todo )
    {
      final TodoItem.Props props = new TodoItem.Props();
      props.key = todo.getId();
      props.todo = todo;
      return props;
    }
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class State
    extends BaseState
  {
    @JsOverlay
    static State create( @Nonnull final String editText )
    {
      final State state = new State();
      state.editText = editText;
      return state;
    }

    String editText;
  }

  private boolean _isEditing;

  @Computed
  boolean isTodoBeingEdited()
  {
    return AppData.viewService.getTodoBeingEdited() == props().todo;
  }

  @Override
  protected void componentInitialize()
  {
    setInitialState( createInitialState() );
  }

  @Action
  State createInitialState()
  {
    return State.create( props().todo.getTitle() );
  }

  @EventHandler( KeyboardEventHandler.class )
  void handleKeyDown( @Nonnull final KeyboardEvent event )
  {
    if ( event.which == App.ESCAPE_KEY )
    {
      onCancel();
    }
    else if ( event.which == App.ENTER_KEY )
    {
      onSubmitTodo();
    }
  }

  @EventHandler( FocusEventHandler.class )
  @Action
  void onSubmitTodo()
  {
    final String val = state().editText;
    final Props props = props();
    if ( null != val && !val.isEmpty() )
    {
      AppData.service.save( props.todo, val );
      AppData.viewService.setTodoBeingEdited( null );
      setState( State.create( val ) );
    }
    else
    {
      AppData.model.destroy( props().todo );
    }
  }

  @EventHandler( FormEventHandler.class )
  @Action
  void onToggle()
  {
    props().todo.toggle();
  }

  @EventHandler( MouseEventHandler.class )
  @Action
  void onEdit()
  {
    AppData.viewService.setTodoBeingEdited( props().todo );
    setState( State.create( props().todo.getTitle() ) );
  }

  @EventHandler( MouseEventHandler.class )
  @Action
  void onDestroy()
  {
    AppData.model.destroy( props().todo );
  }

  @Action
  void onCancel()
  {
    setState( State.create( props().todo.getTitle() ) );
    AppData.viewService.setTodoBeingEdited( null );
  }

  @EventHandler( FormEventHandler.class )
  @Action
  void handleChange( @Nonnull final FormEvent event )
  {
    if ( isTodoBeingEdited() )
    {
      final HTMLInputElement input = Js.cast( event.target );
      setState( State.create( input.value ) );
    }
  }

  @Action( reportParameters = false )
  @Override
  protected void componentDidUpdate( @Nullable final Props prevProps, @Nullable final State prevState )
  {
    super.componentDidUpdate( prevProps, prevState );
    final boolean todoBeingEdited = isTodoBeingEdited();
    if ( !_isEditing && todoBeingEdited )
    {
      _isEditing = true;
      setState( State.create( state().editText ) );
      final HTMLInputElement input = getRefNamed( "editField" );
      assert null != input;
      input.focus();
      input.select();
    }
    else if ( _isEditing && !todoBeingEdited )
    {
      _isEditing = false;
    }
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    final Props props = props();
    final boolean completed = props.todo.isCompleted();
    return li( new HtmlProps().className( classesFor( completed, isTodoBeingEdited() ) ),
               div( new HtmlProps().className( "view" ),
                    input( new InputProps()
                             .className( "toggle" )
                             .type( InputType.checkbox ).checked( completed )
                             .onChange( _onToggle( this ) )
                    ),
                    label( new LabelProps().OnDoubleClick( _onEdit( this ) ),
                           props.todo.getTitle() ),
                    button( new BtnProps()
                              .className( "destroy" )
                              .onClick( _onDestroy( this ) )
                    )
               ),
               input( new InputProps()
                        .ref( "editField" )
                        .className( "edit" )
                        .defaultValue( state().editText )
                        .onBlur( _onSubmitTodo( this ) )
                        .onChange( _handleChange( this ) )
                        .onKeyDown( _handleKeyDown( this ) )
               )
    );
  }

  private static String classesFor( final boolean completed, final boolean editing )
  {
    String cls = completed ? "completed" : "";
    if ( editing )
    {
      if ( !cls.isEmpty() )
      {
        cls += " ";
      }
      cls += "editing";
    }
    return cls;
  }
}
