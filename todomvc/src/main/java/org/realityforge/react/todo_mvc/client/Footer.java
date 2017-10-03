package org.realityforge.react.todo_mvc.client;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.react.todo_mvc.client.model.AppData;
import org.realityforge.react.todo_mvc.client.model.FilterMode;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.arez.ReactArezComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;
import react.dom.events.MouseEventHandler;
import react.dom.proptypes.html.AnchorProps;
import react.dom.proptypes.html.BtnProps;
import react.dom.proptypes.html.HtmlProps;
import static org.realityforge.react.todo_mvc.client.Footer_.*;
import static react.dom.DOM.*;

@ReactComponent
@ArezComponent
class Footer
  extends ReactArezComponent<BaseProps, BaseState>
{
  @EventHandler( MouseEventHandler.class )
  void handleClearCompleted()
  {
    AppData.service.clearCompleted();
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> doRender()
  {
    final int count = AppData.model.totalCount();
    final String activeTodoWord = "item" + ( count == 1 ? "" : "s" );
    final FilterMode filterMode = AppData.viewService.getFilterMode();
    return
      footer( new HtmlProps().className( "footer" ),
              span( new HtmlProps().className( "todo-count" ),
                    strong( Integer.toString( count ) ),
                    text( " " + activeTodoWord + " left" )
              ),
              ul( new HtmlProps().className( "filters" ),
                  li( a( new AnchorProps()
                           .className( selected( FilterMode.ALL == filterMode ) )
                           .href( "#" ), "All" )
                  ),
                  li( a( new AnchorProps()
                           .className( selected( FilterMode.ACTIVE == filterMode ) )
                           .href( "#active" ), "Active" )
                  ),
                  li( a( new AnchorProps()
                           .className( selected( FilterMode.COMPLETED == filterMode ) )
                           .href( "#completed" ), "Completed" )
                  )
              ),
              buildClearButton()
      );
  }

  @Nullable
  private ReactElement<?, ?> buildClearButton()
  {
    if ( AppData.model.completedCount() > 0 )
    {
      return button( new BtnProps().className( "clear-completed" ).onClick( _handleClearCompleted( this ) ),
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
