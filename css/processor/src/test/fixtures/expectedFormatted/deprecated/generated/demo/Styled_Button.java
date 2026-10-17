package demo;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@Generated("react4j.css.processor.CssProcessor")
@SuppressWarnings({"deprecation", "removal"})
@NullMarked
final class Styled_Button extends Button {
  @Override
  String active() {
    return "jcss-demo-Button jcss-demo-Button--active";
  }

  @Override
  String style() {
    return "jcss-demo-Button";
  }
}
