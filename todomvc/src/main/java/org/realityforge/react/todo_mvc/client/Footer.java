package org.realityforge.react.todo_mvc.client;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jetbrains.annotations.Nullable;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;
import react.core.StatelessSideComponent;
import react.dom.events.MouseEventHandler;
import react.dom.proptypes.html.AnchorProps;
import react.dom.proptypes.html.BtnProps;
import react.dom.proptypes.html.HtmlProps;
import static react.dom.DOM.*;

class Footer
  extends StatelessSideComponent<Footer.Props>
{
  @JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
  static class Props
    extends BaseProps
  {
    int count;
    int completedCount;
    String nowShowing;
    MouseEventHandler onClearCompleted;

    @JsOverlay
    public static Props create( @Nonnegative final int count,
                                @Nonnegative final int completedCount,
                                @Nonnull final String nowShowing,
                                @Nonnull final MouseEventHandler onClearCompleted )
    {
      final Props props = new Props();
      props.count = count;
      props.completedCount = completedCount;
      props.nowShowing = nowShowing;
      props.onClearCompleted = onClearCompleted;
      return props;
    }
  }

  Footer( @Nonnull final Component<Props, BaseState> component )
  {
    super( component );
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    final String activeTodoWord = "item" + ( props().count == 1 ? "" : "s" );

    return
      footer( new HtmlProps().className( "footer" ),
              span( new HtmlProps().className( "todo-count" ),
                    strong( null, Integer.toString( props().count ) ),
                    " " + activeTodoWord + " left"
              ),
              ul( new HtmlProps().className( "filters" ),
                  li( null,
                      a( new AnchorProps()
                           .className( selected( props().nowShowing == null ) )
                           .href( "#" ), "All" )
                  ),
                  li( null,
                      a( new AnchorProps()
                           .className( selected( TodoList.NOW_SHOWING_ACTIVE_TODOS.equals( props().nowShowing ) ) )
                           .href( "#active" ), "Active" )
                  ),
                  li( null,
                      a( new AnchorProps()
                           .className( selected( TodoList.NOW_SHOWING_COMPLETED_TODOS.equals( props().nowShowing ) ) )
                           .href( "#completed" ), "Completed" )
                  )
              ),
              buildClearButton( props() )
      );
  }

  @Nullable
  private ReactElement<?, ?> buildClearButton( @Nonnull final Props props )
  {
    if ( props.completedCount > 0 )
    {
      return button( new BtnProps().className( "clear-completed" ).onClick( props.onClearCompleted ),
                     "Clear Completed" );
    }
    else
    {
      return null;
    }
  }

  private static String selected( final boolean condition )
  {
    return condition ? "selected" : "";
  }
}
