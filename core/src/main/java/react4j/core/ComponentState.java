package react4j.core;

/**
 * Enum representing lifecycle method currently being invoked.
 * This is used for invariant checking.
 */
enum ComponentState
{
  INITIAL,
  COMPONENT_WILL_MOUNT,
  COMPONENT_DID_MOUNT,
  COMPONENT_WILL_RECEIVE_PROPS,
  SHOULD_COMPONENT_UPDATE,
  COMPONENT_WILL_UPDATE,
  COMPONENT_DID_UPDATE,
  COMPONENT_WILL_UNMOUNT,
  RENDER,
  UNKNOWN
}
