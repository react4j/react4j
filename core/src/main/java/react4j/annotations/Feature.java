package react4j.annotations;

/**
 * Enum to declare whether a specific feature should be present.
 */
public enum Feature
{
  /**
   * Feature should be present.
   */
  ENABLE,
  /**
   * Feature should not be present.
   */
  DISABLE,
  /**
   * Feature should be present if supporting infrastructure is detected.
   */
  AUTODETECT
}
