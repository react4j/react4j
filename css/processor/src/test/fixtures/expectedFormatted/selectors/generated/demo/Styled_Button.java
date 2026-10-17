package demo;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@NullMarked
final class Styled_Button extends Button {
  @Override
  String cancel() {
    return "jcss-demo-Button jcss-demo-Button--cancel";
  }

  @Override
  String style() {
    return "jcss-demo-Button";
  }

  @Override
  String success() {
    return "jcss-demo-Button jcss-demo-Button--success";
  }
}
