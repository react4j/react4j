package demo;

@java.lang.SuppressWarnings("UnnecessarilyFullyQualified")
@org.jspecify.annotations.NullMarked
final class Styled_Button extends demo.Button {
  @java.lang.Override
  protected java.lang.String cancel() { return "jcss-demo-Button jcss-demo-Button--cancel"; }
  @java.lang.Override
  java.lang.String style() { return "jcss-demo-Button"; }
  @java.lang.Override
  java.lang.String success() { return "jcss-demo-Button jcss-demo-Button--success"; }
}
