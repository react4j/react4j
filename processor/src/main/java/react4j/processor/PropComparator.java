package react4j.processor;

import java.util.Comparator;

/**
 * Sort props.
 * Non-optional props in their declared order come first, then optional props in their
 * declared order and finally the child or children prop.
 */
final class PropComparator
  implements Comparator<PropDescriptor>
{
  static final PropComparator COMPARATOR = new PropComparator();

  private PropComparator()
  {
  }

  @Override
  public int compare( final PropDescriptor o1, final PropDescriptor o2 )
  {
    final String name1 = o1.getName();
    final String name2 = o2.getName();

    if ( name1.equals( "children" ) || name1.equals( "child" ) )
    {
      return 1;
    }
    else if ( name2.equals( "children" ) || name2.equals( "child" ) )
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
