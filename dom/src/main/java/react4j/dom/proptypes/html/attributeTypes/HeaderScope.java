package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

/**
 * An enumerated value to define the cells that the header element relates to.
 */
@Documented
@MagicConstant( valuesFromClass = HeaderScope.class )
public @interface HeaderScope
{
  /**
   * The header relates to all cells of the row it belongs to.
   */
  @Nonnull
  String row = "row";
  /**
   * The header relates to all cells of the column it belongs to.
   */
  @Nonnull
  String col = "col";
  /**
   * The header belongs to a rowgroup and relates to all of its cells. These cells can be
   * placed to the right or the left of the header, depending on the value of the <code>dir</code>
   * attribute in the <code>&lt;table&gt;</code> element.
   */
  @Nonnull
  String rowgroup = "rowgroup";
  /**
   * The header belongs to a colgroup and relates to all of its cells.
   */
  @Nonnull
  String colgroup = "colgroup";
  /**
   * The default value.
   */
  @Nonnull
  String auto = "auto";
}
