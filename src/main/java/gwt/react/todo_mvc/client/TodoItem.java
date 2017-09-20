package gwt.react.todo_mvc.client;

import com.google.gwt.dom.client.InputElement;
import gwt.interop.utils.client.plainobjects.JsPlainObj;
import gwt.interop.utils.shared.functional.JsBiConsumer;
import gwt.react.client.components.Component;
import gwt.react.client.components.SideComponent;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.events.FormEvent;
import gwt.react.client.events.KeyboardEvent;
import gwt.react.client.proptypes.BaseProps;
import gwt.react.client.proptypes.html.BtnProps;
import gwt.react.client.proptypes.html.HtmlProps;
import gwt.react.client.proptypes.html.InputProps;
import gwt.react.client.proptypes.html.LabelProps;
import gwt.react.client.proptypes.html.attributeTypes.InputType;
import gwt.react.todo_mvc.client.model.Todo;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;
import static gwt.interop.utils.client.plainobjects.JsPlainObj.$;
import static gwt.react.client.api.React.DOM.*;

class TodoItem
  extends SideComponent<TodoItem.Props, TodoItem.State>
{
  static final JsConstructorFn<TodoItemWrapper> TYPE = TodoItemWrapper.ctor();

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  public static class Props
    extends BaseProps
  {
    Todo todo;
    boolean isEditing;
    JsBiConsumer<Todo, String> doSave;
    JsBiConsumer<TodoList.Action, Todo> doAction;
  }

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class State
    extends JsPlainObj
  {
    String editText;
  }

  private State newTodoItemState( @Nonnull final String editText )
  {
    return $( new State(), "editText", editText );
  }

  TodoItem( @Nonnull final Component<Props, State> component )
  {
    super( component );
    component.setInitialState( newTodoItemState( props().todo.getTitle() ) );
  }

  private void onSubmitTodo()
  {
    String val = component().state().editText;
    if ( val != null && !val.isEmpty() )
    {
      props().doSave.accept( props().todo, val );

      setState( newTodoItemState( val ) );
    }
    else
    {
      props().doAction.accept( TodoList.Action.DESTROY, props().todo );
    }
  }

  private void onEdit()
  {
    props().doAction.accept( TodoList.Action.EDIT, props().todo );
    setState( newTodoItemState( props().todo.getTitle() ) );
  }

  private void handleKeyDown( @Nonnull final KeyboardEvent event )
  {
    if ( event.which == App.ESCAPE_KEY )
    {
      setState( newTodoItemState( props().todo.getTitle() ) );
      props().doAction.accept( TodoList.Action.CANCEL, props().todo );
    }
    else if ( event.which == App.ENTER_KEY )
    {
      onSubmitTodo();
    }
  }

  private void handleChange( @Nonnull final FormEvent event )
  {
    if ( props().isEditing )
    {
      setState( newTodoItemState( InputElement.as( event.target ).getValue() ) );
    }
  }

  @Override
  protected boolean shouldComponentUpdate( @Nonnull final Props nextProps, @Nonnull final State nextState )
  {
    return ( nextProps.todo != props().todo ||
             nextProps.isEditing != props().isEditing ||
             !nextState.editText.equals( state().editText ) );
  }

  @Override
  protected void componentDidUpdate( @Nonnull final Props prevProps, @Nonnull final Props prevState )
  {
    if ( !prevProps.isEditing && props().isEditing )
    {
      final InputElement input = InputElement.as( (InputElement) refs().get( "editField" ) );
      input.focus();
      input.select();
    }
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    return
      li( new HtmlProps().className( classesFor( props().todo.isCompleted(), props().isEditing ) ),
          div( new HtmlProps().className( "view" ),
               input( new InputProps()
                        .className( "toggle" )
                        .type( InputType.checkbox ).checked( props().todo.isCompleted() )
                        .onChange( e -> props().doAction.accept( TodoList.Action.TOGGLE, props().todo ) ) ),
               label( new LabelProps()
                        .OnDoubleClick( e -> onEdit() ), props().todo.getTitle() ),
               button( new BtnProps()
                         .className( "destroy" )
                         .onClick( e -> props().doAction.accept( TodoList.Action.DESTROY, props().todo ) ) )
          ),
          input( new InputProps()
                   .ref( "editField" )
                   .className( "edit" )
                   .defaultValue( state().editText )
                   .onBlur( e -> onSubmitTodo() )
                   .onChange( this::handleChange )
                   .onKeyDown( this::handleKeyDown ) )
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
