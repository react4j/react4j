package react4j.processor;

enum StepMethodType
{
  /**
   * Does method stay at same step. i.e. An optional step.
   */
  STAY,
  /**
   * Does method result in move to next step. i.e. A required step.
   */
  ADVANCE,
  /**
   * Does method result in terminate of build chain.
   */
  TERMINATE
}
