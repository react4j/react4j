package react4j.examples.arez.step2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactElement;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.examples.arez.step2.model.AppData;
import static react4j.dom.DOM.*;
import static react4j.examples.arez.step2.FooterTodoCount_.*;

@ReactComponent
class FooterTodoCount
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nonnull
  static ReactElement<BaseProps, NativeReactComponent> create()
  {
    return _create();
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> renderAsElement()
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
