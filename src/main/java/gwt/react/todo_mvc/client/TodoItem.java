package gwt.react.todo_mvc.client;

import elemental2.dom.HTMLInputElement;
import gwt.react.client.components.BaseProps;
import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.components.SideComponent;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.events.FormEvent;
import gwt.react.client.events.KeyboardEvent;
import gwt.react.client.proptypes.html.BtnProps;
import gwt.react.client.proptypes.html.HtmlProps;
import gwt.react.client.proptypes.html.InputProps;
import gwt.react.client.proptypes.html.LabelProps;
import gwt.react.client.proptypes.html.attributeTypes.InputType;
import gwt.react.todo_mvc.client.model.Todo;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;
import static gwt.react.client.api.React.DOM.*;

class TodoItem
  extends SideComponent<TodoItem.Props, TodoItem.State>
{
  static final JsConstructorFn<TodoItemWrapper> TYPE = TodoItemWrapper.ctor();

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
    public static Props create( @Nonnull final Todo todo,
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

  TodoItem( @Nonnull final Component<Props, State> component )
  {
    super( component );
    component.setInitialState( State.create( props().todo.getTitle() ) );
  }

  private void onSubmitTodo()
  {
    final String val = component().state().editText;
    if ( null != val && !val.isEmpty() )
    {
      props().doSave.accept( props().todo, val );

      setState( State.create( val ) );
    }
    else
    {
      props().doAction.accept( TodoList.ActionType.DESTROY, props().todo );
    }
  }

  private void onEdit()
  {
    props().doAction.accept( TodoList.ActionType.EDIT, props().todo );
    setState( State.create( props().todo.getTitle() ) );
  }

  private void handleKeyDown( @Nonnull final KeyboardEvent event )
  {
    if ( event.which == App.ESCAPE_KEY )
    {
      setState( State.create( props().todo.getTitle() ) );
      props().doAction.accept( TodoList.ActionType.CANCEL, props().todo );
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
      final HTMLInputElement input = Js.cast( event.target );
      setState( State.create( input.value ) );
    }
  }

  @Override
  public  boolean shouldComponentUpdate( @Nonnull final Props nextProps, @Nonnull final State nextState )
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
      final HTMLInputElement input = getRefNamed( "editField" );
      assert null != input;
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
                        .onChange( e -> props().doAction.accept( TodoList.ActionType.TOGGLE, props().todo ) )
               ),
               label( new LabelProps()
                        .OnDoubleClick( e -> onEdit() ), props().todo.getTitle()
               ),
               button( new BtnProps()
                         .className( "destroy" )
                         .onClick( e -> props().doAction.accept( TodoList.ActionType.DESTROY, props().todo ) )
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
