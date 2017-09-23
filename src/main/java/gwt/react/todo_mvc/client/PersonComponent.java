package gwt.react.todo_mvc.client;

import gwt.react.client.components.BaseProps;
import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.proptypes.html.HtmlProps;
import gwt.react.todo_mvc.client.model.PersonModel;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;
import static gwt.react.client.api.React.DOM.h1;
import static gwt.react.client.api.React.DOM.h4;
import static gwt.react.client.api.React.DOM.section;

public class PersonComponent
  extends ArezComponent<PersonComponent.Props, BaseState>
{
  static final JsConstructorFn<PersonComponentWrapper> TYPE = PersonComponentWrapper.ctor();

  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class Props
    extends BaseProps
  {
    int count;
    PersonModel personModel;

    @JsOverlay
    public static Props create( @Nonnegative final int count )
    {
      final Props props = new Props();
      props.count = count;
      props.personModel = TestData.P1;
      return props;
    }
  }

  public PersonComponent( @Nonnull final Component<Props, BaseState> component )
  {
    super( component );
  }

  @Nonnull
  @Override
  protected String getTypeName()
  {
    return "Person";
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    return h4( null, props().personModel.getFullName() );
  }
}
