package react4j.dom.proptypes.html.attributeTypes;

public enum Target
{
  blank( "_blank" ),
  self( "_self" ),
  parent( "_parent" ),
  top( "_top" );

  private String val;

  Target( String val )
  {
    this.val = val;
  }

  String getVal()
  {
    return val;
  }
}
