package react4j.examples.arez.step1;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.examples.arez.step1.model.AppData;
import react4j.examples.arez.step1.model.FilterMode;
import static react4j.dom.DOM.*;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class Footer
{
  @Render
  ReactNode render()
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
              button( new BtnProps().className( "clear-completed" ).onClick( e -> AppData.service.clearCompleted() ),
                      "Clear Completed" ) :
              null
      );
  }
}
