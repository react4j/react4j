package react4j.processor;

import java.util.Arrays;
import java.util.List;

final class Constants
{
  static final String DAGGER_BINDS_CLASSNAME = "dagger.Binds";
  static final String DAGGER_SUBCOMPONENT_CLASSNAME = "dagger.Subcomponent";
  static final String DAGGER_MODULE_CLASSNAME = "dagger.Module";
  static final String COMPONENT_CLASSNAME = "react4j.Component";
  static final String PROP_ANNOTATION_CLASSNAME = "react4j.annotations.Prop";
  static final String PROP_REF_ANNOTATION_CLASSNAME = "react4j.annotations.PropRef";
  static final String PROP_DEFAULT_ANNOTATION_CLASSNAME = "react4j.annotations.PropDefault";
  static final String PROP_VALIDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PropValidate";
  static final String ON_PROP_CHANGE_ANNOTATION_CLASSNAME = "react4j.annotations.OnPropChange";
  static final String REACT_COMPONENT_ANNOTATION_CLASSNAME = "react4j.annotations.ReactComponent";
  static final String PRE_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PreUpdate";
  static final String POST_UPDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PostUpdate";
  static final String INJECT_ANNOTATION_CLASSNAME = "javax.inject.Inject";
  static final String AREZ_COMPONENT_ANNOTATION_CLASSNAME = "arez.annotations.ArezComponent";
  static final String OBSERVE_ANNOTATION_CLASSNAME = "arez.annotations.Observe";
  static final String DEPENDENCY_ANNOTATION_CLASSNAME = "arez.annotations.Dependency";
  static final String MEMOIZE_ANNOTATION_CLASSNAME = "arez.annotations.Memoize";
  static final String ACTION_ANNOTATION_CLASSNAME = "arez.annotations.Action";
  static final String NONNULL_ANNOTATION_CLASSNAME = "javax.annotation.Nonnull";
  static final String NULLABLE_ANNOTATION_CLASSNAME = "javax.annotation.Nullable";
  static final String DEPRECATED_ANNOTATION_CLASSNAME = "java.lang.Deprecated";
  static final String COMPONENT_DID_MOUNT = "componentDidMount";
  static final String SHOULD_COMPONENT_UPDATE = "shouldComponentUpdate";
  static final String COMPONENT_DID_UPDATE = "componentDidUpdate";
  static final String COMPONENT_PRE_UPDATE = "componentPreUpdate";
  static final String COMPONENT_WILL_UNMOUNT = "componentWillUnmount";
  static final String COMPONENT_DID_CATCH = "componentDidCatch";
  static final List<String> LIFECYCLE_METHODS =
    Arrays.asList( COMPONENT_DID_MOUNT,
                   SHOULD_COMPONENT_UPDATE,
                   COMPONENT_PRE_UPDATE,
                   COMPONENT_DID_UPDATE,
                   COMPONENT_DID_CATCH,
                   COMPONENT_WILL_UNMOUNT );

  private Constants()
  {
  }
}
