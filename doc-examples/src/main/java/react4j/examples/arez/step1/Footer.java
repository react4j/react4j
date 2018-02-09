package react4j.examples.arez.step1;

import javax.annotation.Nullable;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.examples.arez.step1.model.AppData;
import react4j.examples.arez.step1.model.FilterMode;
import static react4j.dom.DOM.*;
import static react4j.examples.arez.step1.Footer_.*;

@ReactComponent
abstract class Footer
  extends ReactArezComponent
{
  @EventHandler( MouseEventHandler.class )
  void handleClearCompleted()
  {
    AppData.service.clearCompleted();
  }

  @Nullable
  @Override
  protected ReactNode render()
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
                           .className( FilterMode.ALL == filterMode ? "selected" : "" )
                           .href( "#" ), "All" )
                  ),
                  li( a( new AnchorProps()
                           .className( FilterMode.ACTIVE == filterMode ? "selected" : "" )
                           .href( "#active" ), "Active" )
                  ),
                  li( a( new AnchorProps()
                           .className( FilterMode.COMPLETED == filterMode ? "selected" : "" )
                           .href( "#completed" ), "Completed" )
                  )
              ),
              AppData.model.completedCount() > 0 ?
              button( new BtnProps().className( "clear-completed" ).onClick( _handleClearCompleted( this ) ),
                      "Clear Completed" ) :
              null
      );
  }
}
