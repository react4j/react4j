package react4j.core;

/**
 * Enum representing lifecycle method currently being invoked.
 * This is used for invariant checking.
 */
enum LifecycleMethod
{
  COMPONENT_POST_CONSTRUCT,
  COMPONENT_DID_MOUNT,
  SHOULD_COMPONENT_UPDATE,
  COMPONENT_DID_UPDATE,
  COMPONENT_DID_CATCH,
  COMPONENT_WILL_UNMOUNT,
  RENDER,
  UNKNOWN
}
