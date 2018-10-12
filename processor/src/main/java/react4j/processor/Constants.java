package react4j.processor;

import java.util.Arrays;
import java.util.List;

final class Constants
{
  static final String DAGGER_SUBCOMPONENT_CLASSNAME = "dagger.Subcomponent";
  static final String DAGGER_MODULE_CLASSNAME = "dagger.Module";
  static final String DAGGER_PROVIDES_CLASSNAME = "dagger.Provides";
  static final String JS_FUNCTION_CLASSNAME = "jsinterop.annotations.JsFunction";
  static final String COMPONENT_CLASSNAME = "react4j.Component";
  static final String CALLBACK_ANNOTATION_CLASSNAME = "react4j.annotations.Callback";
  static final String PROP_ANNOTATION_CLASSNAME = "react4j.annotations.Prop";
  static final String PROP_DEFAULT_ANNOTATION_CLASSNAME = "react4j.annotations.PropDefault";
  static final String PROP_VALIDATE_ANNOTATION_CLASSNAME = "react4j.annotations.PropValidate";
  static final String REACT_COMPONENT_ANNOTATION_CLASSNAME = "react4j.annotations.ReactComponent";
  static final String INJECT_ANNOTATION_CLASSNAME = "javax.inject.Inject";
  static final String AREZ_COMPONENT_ANNOTATION_CLASSNAME = "arez.annotations.ArezComponent";
  static final String OBSERVE_ANNOTATION_CLASSNAME = "arez.annotations.Observe";
  static final String DEPENDENCY_ANNOTATION_CLASSNAME = "arez.annotations.Dependency";
  static final String COMPUTED_ANNOTATION_CLASSNAME = "arez.annotations.Computed";
  static final String MEMOIZE_ANNOTATION_CLASSNAME = "arez.annotations.Memoize";
  static final String ACTION_ANNOTATION_CLASSNAME = "arez.annotations.Action";
  static final String NONNULL_ANNOTATION_CLASSNAME = "javax.annotation.Nonnull";
  static final String NULLABLE_ANNOTATION_CLASSNAME = "javax.annotation.Nullable";
  static final String DEPRECATED_ANNOTATION_CLASSNAME = "java.lang.Deprecated";
  static final String COMPONENT_DID_MOUNT = "componentDidMount";
  static final String COMPONENT_DID_UPDATE = "componentDidUpdate";
  static final String SHOULD_COMPONENT_UPDATE = "shouldComponentUpdate";
  static final String COMPONENT_WILL_UNMOUNT = "componentWillUnmount";
  static final String COMPONENT_DID_CATCH = "componentDidCatch";
  static final List<String> LIFECYCLE_METHODS =
    Arrays.asList( COMPONENT_DID_MOUNT,
                   COMPONENT_DID_UPDATE,
                   COMPONENT_WILL_UNMOUNT,
                   COMPONENT_DID_CATCH,
                   SHOULD_COMPONENT_UPDATE );

  private Constants()
  {
  }
}
