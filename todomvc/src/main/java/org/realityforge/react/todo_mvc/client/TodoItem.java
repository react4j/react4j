package org.realityforge.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.Container;
import org.realityforge.react.todo_mvc.client.model.Todo;
import react.annotations.ReactComponent;
import react.arez.ArezComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;
import react.dom.DOMElement;
import react.dom.events.FormEvent;
import react.dom.events.KeyboardEvent;
import react.dom.proptypes.html.BtnProps;
import react.dom.proptypes.html.HtmlProps;
import react.dom.proptypes.html.InputProps;
import react.dom.proptypes.html.LabelProps;
import react.dom.proptypes.html.attributeTypes.InputType;
import static react.dom.DOM.*;

@ReactComponent
@Container
class TodoItem
  extends ArezComponent<TodoItem.Props, TodoItem.State>
{
  @JsFunction
  public interface JsBiConsumer<A1, A2>
  {
    void accept( A1 arg, A2 arg2 );
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  public static class Props
    extends BaseProps
  {
    Todo todo;
    boolean isEditing;
    JsBiConsumer<Todo, String> doSave;
    JsBiConsumer<TodoList.ActionType, Todo> doAction;

    @JsOverlay
    static Props create( @Nonnull final Todo todo,
                         @Nonnull final JsBiConsumer<Todo, String> doSave,
                         @Nonnull final JsBiConsumer<TodoList.ActionType, Todo> doAction,
                         final boolean isEditing )
    {
      final TodoItem.Props props = new TodoItem.Props();
      props.key = todo.getId();
      props.todo = todo;
      props.doAction = doAction;
      props.doSave = doSave;
      props.isEditing = isEditing;
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

  @Override
  protected void componentInitialize()
  {
    setInitialState( () -> State.create( props().todo.getTitle() ) );
  }

  private void handleKeyDown( @Nonnull final KeyboardEvent event )
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

  @Action
  void onSubmitTodo()
  {
    final String val = state().editText;
    final Props props = props();
    if ( null != val && !val.isEmpty() )
    {
      onSave( val, props );
    }
    else
    {
      onDestroy();
    }
  }

  private void onSave( final String val, final Props props )
  {
    props.doSave.accept( props.todo, val );
    setState( State.create( val ) );
  }

  @Action
  void onToggle()
  {
    final Props props = props();
    props.doAction.accept( TodoList.ActionType.TOGGLE, props.todo );
  }

  @Action
  void onEdit()
  {
    props().doAction.accept( TodoList.ActionType.EDIT, props().todo );
    setState( State.create( props().todo.getTitle() ) );
  }

  @Action
  void onDestroy()
  {
    final Props props = props();
    props.doAction.accept( TodoList.ActionType.DESTROY, props.todo );
  }

  @Action
  void onCancel()
  {
    setState( State.create( props().todo.getTitle() ) );
    props().doAction.accept( TodoList.ActionType.CANCEL, props().todo );
  }

  private void handleChange( @Nonnull final FormEvent event )
  {
    if ( props().isEditing )
    {
      final HTMLInputElement input = Js.cast( event.target );
      setState( State.create( input.value ) );
    }
  }

  @Override
  protected void componentDidUpdate( @Nonnull final Props prevProps, @Nonnull final State prevState )
  {
    if ( !prevProps.isEditing && component().props().isEditing )
    {
      final HTMLInputElement input = getRefNamed( "editField" );
      assert null != input;
      input.focus();
      input.select();
    }
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    final Props props = props();
    final boolean completed = props.todo.isCompleted();
    final DOMElement<HtmlProps> element =
      li( new HtmlProps().className( classesFor( completed, props.isEditing ) ),
          div( new HtmlProps().className( "view" ),
               input( new InputProps()
                        .className( "toggle" )
                        .type( InputType.checkbox ).checked( completed )
                        .onChange( e -> onToggle() )
               ),
               label( new LabelProps().OnDoubleClick( e -> onEdit() ),
                      props.todo.getTitle() ),
               button( new BtnProps()
                         .className( "destroy" )
                         .onClick( e -> onDestroy() )
               )
          ),
          input( new InputProps()
                   .ref( "editField" )
                   .className( "edit" )
                   .defaultValue( state().editText )
                   .onBlur( e -> onSubmitTodo() )
                   .onChange( this::handleChange )
                   .onKeyDown( this::handleKeyDown )
          )
      );
    return element;
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
