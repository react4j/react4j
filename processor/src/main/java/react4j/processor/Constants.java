package react4j.processor;

import javax.annotation.Nonnull;

final class Constants
{
  @Nonnull
  static final String COMPONENT_CLASSNAME = "react4j.Component";
  @Nonnull
  static final String ERROR_INFO_CLASSNAME = "react4j.ReactErrorInfo";
  @Nonnull
  static final String KEYED_CLASSNAME = "react4j.Keyed";
  @Nonnull
  static final String PROP_ANNOTATION_CLASSNAME = "react4j.annotations.Prop";
  @Nonnull
  static final String SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME = "react4j.annotations.SuppressReact4jWarnings";
  @Nonnull
  static final String PROP_REF_ANNOTATION_CLASSNAME = "react4j.annotations.PropRef";
  @Nonnull
  static final String PROP_DEFAULT_ANNOTATION_CLASSNAME = "react4j.annotations.PropDefault";
  @Nonnull
  static final String PROP_VALIDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PropValidate";
  @Nonnull
  static final String ON_ERROR_ANNOTATION_CLASSNAME = "react4j.annotations.OnError";
  @Nonnull
  static final String ON_PROP_CHANGE_ANNOTATION_CLASSNAME = "react4j.annotations.OnPropChange";
  @Nonnull
  static final String REACT_COMPONENT_ANNOTATION_CLASSNAME = "react4j.annotations.ReactComponent";
  @Nonnull
  static final String POST_MOUNT_ANNOTATION_CLASSNAME = "react4j.annotations.PostMount";
  @Nonnull
  static final String POST_MOUNT_OR_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PostMountOrUpdate";
  @Nonnull
  static final String POST_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PostUpdate";
  @Nonnull
  static final String PRE_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PreUpdate";
  @Nonnull
  static final String ACT_AS_COMPONENT_ANNOTATION_CLASSNAME = "arez.annotations.ActAsComponent";
  @Nonnull
  static final String AREZ_COMPONENT_ANNOTATION_CLASSNAME = "arez.annotations.ArezComponent";
  @Nonnull
  static final String REPOSITORY_ANNOTATION_CLASSNAME = "arez.annotations.Repository";
  @Nonnull
  static final String COMPONENT_ID_ANNOTATION_CLASSNAME = "arez.annotations.ComponentId";
  @Nonnull
  static final String COMPONENT_ID_REF_ANNOTATION_CLASSNAME = "arez.annotations.ComponentIdRef";
  @Nonnull
  static final String OBSERVE_ANNOTATION_CLASSNAME = "arez.annotations.Observe";
  @Nonnull
  static final String MEMOIZE_ANNOTATION_CLASSNAME = "arez.annotations.Memoize";
  @Nonnull
  static final String POST_CONSTRUCT_ANNOTATION_CLASSNAME = "arez.annotations.PostConstruct";
  @Nonnull
  static final String COMPONENT_DEPENDENCY_ANNOTATION_CLASSNAME = "arez.annotations.ComponentDependency";
  @Nonnull
  static final String CASCADE_DISPOSE_ANNOTATION_CLASSNAME = "arez.annotations.CascadeDispose";
  @Nonnull
  static final String JSR_330_INJECT_CLASSNAME = "javax.inject.Inject";
  @Nonnull
  static final String STING_INJECTABLE_CLASSNAME = "sting.Injectable";
  @Nonnull
  static final String STING_EAGER_CLASSNAME = "sting.Eager";
  @Nonnull
  static final String STING_NAMED_CLASSNAME = "sting.Named";
  @Nonnull
  static final String STING_CONTRIBUTE_TO_CLASSNAME = "sting.ContributeTo";
  @Nonnull
  static final String JSR_330_NAMED_CLASSNAME = "javax.inject.Named";
  @Nonnull
  static final String JS_ERROR_CLASSNAME = "elemental2.core.JsError";
  @Nonnull
  static final String WARNING_MUTABLE_PROP_ACCESSED_IN_POST_CONSTRUCT =
    "React4j:MutablePropAccessedInPostConstruct";
  @Nonnull
  static final String WARNING_PUBLIC_METHOD = "React4j:PublicMethod";
  @Nonnull
  static final String WARNING_PROTECTED_METHOD = "React4j:ProtectedMethod";

  private Constants()
  {
  }
}
