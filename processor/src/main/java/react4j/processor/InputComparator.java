package react4j.processor;

import java.util.Comparator;
import javax.annotation.Nonnull;

/**
 * Sort inputs.
 * Non-optional inputs come first, then optional inputs and finally the child or children input.
 * Within each group, constructor parameter inputs precede method inputs.
 */
final class InputComparator
  implements Comparator<InputDescriptor>
{
  static final InputComparator COMPARATOR = new InputComparator();

  private InputComparator()
  {
  }

  @Override
  public int compare( @Nonnull final InputDescriptor o1, @Nonnull final InputDescriptor o2 )
  {
    if ( o1.isSpecialChildrenInput() )
    {
      return 1;
    }
    else if ( o2.isSpecialChildrenInput() )
    {
      return -1;
    }
    else if ( o2.isOptional() && o1.isRequired() )
    {
      return -1;
    }
    else if ( o1.isOptional() && o2.isRequired() )
    {
      return 1;
    }
    else if ( o1.isImmutable() && o2.isMethodInput() )
    {
      return -1;
    }
    else if ( o1.isMethodInput() && o2.isImmutable() )
    {
      return 1;
    }
    else
    {
      return 0;
    }
  }
}
