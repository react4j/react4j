package react4j.todomvc;

import javax.annotation.Nullable;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.Computed;
import react4j.todomvc.model.AppData;
import react4j.todomvc.model.FilterMode;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactElement;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import static react4j.todomvc.Footer_.*;
import static react4j.dom.DOM.*;

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

  @Computed
  boolean hasCompletedItems()
  {
    return AppData.model.completedCount() > 0;
  }

  @Nullable
  private ReactElement<?, ?> buildClearButton()
  {
    if ( hasCompletedItems() )
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
