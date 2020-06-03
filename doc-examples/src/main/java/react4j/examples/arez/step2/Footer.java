package react4j.examples.arez.step2;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.examples.arez.step2.model.AppData;
import react4j.examples.arez.step2.model.FilterMode;
import static react4j.dom.DOM.*;

@View( type = View.Type.TRACKING )
abstract class Footer
{
  @Nullable
  @Render
  ReactNode render()
  {
    final FilterMode filterMode = AppData.viewService.getFilterMode();
    return
      footer( new HtmlProps().className( "footer" ),
              FooterTodoCountBuilder.build(),
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
              hasCompletedItems() ?
              button( new BtnProps().className( "clear-completed" ).onClick( e -> AppData.service.clearCompleted() ),
                      "Clear Completed" ) :
              null
      );
  }

  @Memoize
  boolean hasCompletedItems()
  {
    return AppData.model.completedCount() > 0;
  }
}
