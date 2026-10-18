package react4j.css.processor;

import com.helger.css.ICSSWriteable;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberMath;
import com.helger.css.decl.CSSExpressionMemberMathProduct;
import com.helger.css.decl.CSSExpressionMemberMathUnitProduct;
import java.util.function.Consumer;

/** Walks expression trees, including the parser's separate calc arithmetic model. */
final class CssExpressions {
    private CssExpressions() {}

    static void visit(ICSSWriteable member, Consumer<ICSSWriteable> visitor) {
        visitor.accept(member);
        if (member instanceof CSSExpression expression) {
            expression.getAllMembers().forEach(child -> visit(child, visitor));
        } else if (member instanceof CSSExpressionMemberFunction function) {
            var expression = function.getExpression();
            if (null != expression) {
                visit(expression, visitor);
            }
        } else if (member instanceof CSSExpressionMemberMath math) {
            math.getAllMembers().forEach(child -> visit(child, visitor));
        } else if (member instanceof CSSExpressionMemberMathProduct product) {
            product.getAllMembers().forEach(child -> visit(child, visitor));
        } else if (member instanceof CSSExpressionMemberMathUnitProduct unit) {
            visit(unit.getProduct(), visitor);
        }
    }
}
