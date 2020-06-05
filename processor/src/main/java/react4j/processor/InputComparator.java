package react4j.processor;

import java.util.Comparator;

/**
 * Sort inputs.
 * Non-optional inputs in their declared order come first, then optional inputs in their
 * declared order and finally the child or children input.
 */
final class InputComparator
  implements Comparator<InputDescriptor>
{
  static final InputComparator COMPARATOR = new InputComparator();

  private InputComparator()
  {
  }

  @Override
  public int compare( final InputDescriptor o1, final InputDescriptor o2 )
  {
    if ( o1.isSpecialChildrenInput() )
    {
      return 1;
    }
    else if ( o2.isSpecialChildrenInput() )
    {
      return -1;
    }
    else if ( o2.isOptional() && !o1.isOptional() )
    {
      return -1;
    }
    else if ( o1.isOptional() && !o2.isOptional() )
    {
      return 1;
    }
    else
    {
      return 0;
    }
  }
}
