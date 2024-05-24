package react4j.processor;

import javax.annotation.Nonnull;

final class Constants
{
  @Nonnull
  static final String ERROR_INFO_CLASSNAME = "react4j.ReactErrorInfo";
  @Nonnull
  static final String VNODE_CLASSNAME = "react4j.ReactNode";
  @Nonnull
  static final String KEYED_CLASSNAME = "react4j.Keyed";
  @Nonnull
  static final String RENDER_CLASSNAME = "react4j.annotations.Render";
  @Nonnull
  static final String INPUT_CLASSNAME = "react4j.annotations.Input";
  @Nonnull
  static final String PUBLISH_CLASSNAME = "react4j.annotations.Publish";
  @Nonnull
  static final String PRE_RENDER_CLASSNAME = "react4j.annotations.PreRender";
  @Nonnull
  static final String POST_RENDER_CLASSNAME = "react4j.annotations.PostRender";
  @Nonnull
  static final String SUPPRESS_REACT4J_WARNINGS_CLASSNAME = "react4j.annotations.SuppressReact4jWarnings";
  @Nonnull
  static final String INPUT_REF_CLASSNAME = "react4j.annotations.InputRef";
  @Nonnull
  static final String INPUT_DEFAULT_CLASSNAME = "react4j.annotations.InputDefault";
  @Nonnull
  static final String INPUT_VALIDATE_CLASSNAME = "react4j.annotations.InputValidate";
  @Nonnull
  static final String ON_ERROR_CLASSNAME = "react4j.annotations.OnError";
  @Nonnull
  static final String ON_INPUT_CHANGE_CLASSNAME = "react4j.annotations.OnInputChange";
  @Nonnull
  static final String VIEW_CLASSNAME = "react4j.annotations.View";
  @Nonnull
  static final String POST_MOUNT_CLASSNAME = "react4j.annotations.PostMount";
  @Nonnull
  static final String POST_MOUNT_OR_UPDATE_CLASSNAME = "react4j.annotations.PostMountOrUpdate";
  @Nonnull
  static final String POST_UPDATE_CLASSNAME = "react4j.annotations.PostUpdate";
  @Nonnull
  static final String PRE_UPDATE_CLASSNAME = "react4j.annotations.PreUpdate";
  @Nonnull
  static final String SCHEDULE_RENDER_CLASSNAME = "react4j.annotations.ScheduleRender";
  @Nonnull
  static final String IDENTIFIABLE_CLASSNAME = "arez.component.Identifiable";
  @Nonnull
  static final String ACT_AS_COMPONENT_CLASSNAME = "arez.annotations.ActAsComponent";
  @Nonnull
  static final String AREZ_COMPONENT_CLASSNAME = "arez.annotations.ArezComponent";
  @Nonnull
  static final String OBSERVE_CLASSNAME = "arez.annotations.Observe";
  @Nonnull
  static final String MEMOIZE_CLASSNAME = "arez.annotations.Memoize";
  @Nonnull
  static final String POST_CONSTRUCT_CLASSNAME = "arez.annotations.PostConstruct";
  @Nonnull
  static final String STING_INJECTABLE_CLASSNAME = "sting.Injectable";
  @Nonnull
  static final String STING_EAGER_CLASSNAME = "sting.Eager";
  @Nonnull
  static final String STING_NAMED_CLASSNAME = "sting.Named";
  @Nonnull
  static final String STING_CONTRIBUTE_TO_CLASSNAME = "sting.ContributeTo";
  @Nonnull
  static final String JS_ERROR_CLASSNAME = "akasha.core.JsError";
  @Nonnull
  static final String WARNING_MUTABLE_INPUT_ACCESSED_IN_POST_CONSTRUCT =
    "React4j:MutableInputAccessedInPostConstruct";
  @Nonnull
  static final String WARNING_PUBLIC_METHOD = "React4j:PublicMethod";
  @Nonnull
  static final String WARNING_PROTECTED_METHOD = "React4j:ProtectedMethod";
  @Nonnull
  static final String WARNING_FINAL_METHOD = "React4j:FinalMethod";

  private Constants()
  {
  }
}
