package react4j.processor;

final class Constants
{
  static final String COMPONENT_CLASSNAME = "react4j.Component";
  static final String ERROR_INFO_CLASSNAME = "react4j.ReactErrorInfo";
  static final String KEYED_CLASSNAME = "react4j.Keyed";
  static final String PROP_ANNOTATION_CLASSNAME = "react4j.annotations.Prop";
  static final String SUPPRESS_REACT4J_WARNINGS_ANNOTATION_CLASSNAME = "react4j.annotations.SuppressReact4jWarnings";
  static final String PROP_REF_ANNOTATION_CLASSNAME = "react4j.annotations.PropRef";
  static final String PROP_DEFAULT_ANNOTATION_CLASSNAME = "react4j.annotations.PropDefault";
  static final String PROP_VALIDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PropValidate";
  static final String ON_ERROR_ANNOTATION_CLASSNAME = "react4j.annotations.OnError";
  static final String ON_PROP_CHANGE_ANNOTATION_CLASSNAME = "react4j.annotations.OnPropChange";
  static final String REACT_COMPONENT_ANNOTATION_CLASSNAME = "react4j.annotations.ReactComponent";
  static final String PRE_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PreUpdate";
  static final String POST_MOUNT_OR_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PostMountOrUpdate";
  static final String POST_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PostUpdate";
  static final String POST_MOUNT_ANNOTATION_CLASSNAME = "react4j.annotations.PostMount";
  static final String ACT_AS_COMPONENT_ANNOTATION_CLASSNAME = "arez.annotations.ActAsComponent";
  static final String AREZ_COMPONENT_ANNOTATION_CLASSNAME = "arez.annotations.ArezComponent";
  static final String REPOSITORY_ANNOTATION_CLASSNAME = "arez.annotations.Repository";
  static final String COMPONENT_ID_ANNOTATION_CLASSNAME = "arez.annotations.ComponentId";
  static final String COMPONENT_ID_REF_ANNOTATION_CLASSNAME = "arez.annotations.ComponentIdRef";
  static final String OBSERVE_ANNOTATION_CLASSNAME = "arez.annotations.Observe";
  static final String MEMOIZE_ANNOTATION_CLASSNAME = "arez.annotations.Memoize";
  static final String PRIORITY_OVERRIDE_ANNOTATION_CLASSNAME = "arez.annotations.PriorityOverride";
  static final String POST_CONSTRUCT_ANNOTATION_CLASSNAME = "arez.annotations.PostConstruct";
  static final String COMPONENT_DEPENDENCY_ANNOTATION_CLASSNAME = "arez.annotations.ComponentDependency";
  static final String CASCADE_DISPOSE_ANNOTATION_CLASSNAME = "arez.annotations.CascadeDispose";
  static final String PER_INSTANCE_ANNOTATION_CLASSNAME = "arez.annotations.PerInstance";
  static final String NONNULL_ANNOTATION_CLASSNAME = "javax.annotation.Nonnull";
  static final String NULLABLE_ANNOTATION_CLASSNAME = "javax.annotation.Nullable";
  static final String DEPRECATED_ANNOTATION_CLASSNAME = "java.lang.Deprecated";
  static final String JS_ERROR_CLASSNAME = "elemental2.core.JsError";
  static final String MUTABLE_PROP_ACCESSED_IN_POST_CONSTRUCT_SUPPRESSION =
    "React4j:MutablePropAccessedInPostConstruct";

  private Constants()
  {
  }
}
