package react4j.examples.arez.step2;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.examples.arez.step2.model.AppData;
import static react4j.dom.DOM.*;

@ReactComponent
abstract class FooterTodoCount
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    final int count = AppData.model.totalCount();
    final String activeTodoWord = "item" + ( count == 1 ? "" : "s" );
    return
      span( new HtmlProps().className( "todo-count" ),
            strong( Integer.toString( count ) ),
            text( " " + activeTodoWord + " left" )
      );
  }
}
