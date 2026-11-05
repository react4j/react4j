package react4j.generation;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.jspecify.annotations.NullMarked;

/** Applies React4j's patches to the pinned CSS parser sources. */
@NullMarked
public final class CssParserSources {
    private CssParserSources() {}

    public static void main(String[] args) throws Exception {
        try (var input = new ZipFile(args[0]);
                var output = new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))) {
            var entries = Collections.list(input.entries());
            entries.sort(Comparator.comparing(ZipEntry::getName));
            for (var entry : entries) {
                if (!entry.getName().endsWith(".java")) {
                    continue;
                }
                byte[] content;
                try (var stream = input.getInputStream(entry)) {
                    content = stream.readAllBytes();
                }
                String source = new String(content, StandardCharsets.UTF_8);
                source = switch (entry.getName()) {
                    case "com/helger/css/parser/ParserCSS30.java" -> nestedGroups(repair(source));
                    case "com/helger/css/parser/ParserCSS30TokenManager.java" -> propertyComments(source);
                    case "com/helger/css/decl/CSSUnknownRule.java" -> structuredRule(source);
                    case "com/helger/css/decl/CSSNestedDeclarations.java" ->
                        replaceOnce(source, "ICSSNestedRule,", "ICSSNestedRule, ICSSTopLevelRule,");
                    case "com/helger/css/handler/CSSHandler.java" ->
                        replaceOnce(
                                source,
                                "if (!ECSSNodeType.ROOT.isNode (aNode))",
                                "if (!ECSSNodeType.ROOT.isNode (aNode)"
                                        + " && !ECSSNodeType.STYLEDECLARATIONLISTWITHNESTED.isNode (aNode))");
                    case "com/helger/css/decl/CSSLayerRule.java" ->
                        replaceOnce(
                                source, "if (nRuleCount == 0)", "if (nRuleCount == 0 && m_aSelectors.isNotEmpty ())");
                    case "com/helger/css/handler/CSSNodeToDomainObject.java" -> conditionalChildren(source);
                    case "com/helger/css/decl/visit/CSSVisitor.java" -> visitGroups(source);
                    default -> source;
                };
                content = source.getBytes(StandardCharsets.UTF_8);
                var target = new ZipEntry(entry.getName());
                target.setTime(0);
                output.putNextEntry(target);
                output.write(content);
                output.closeEntry();
            }
        }
        try (var input = new ZipFile(args[2]);
                var output = new ZipOutputStream(Files.newOutputStream(Path.of(args[3])))) {
            var entries = Collections.list(input.entries());
            entries.sort(Comparator.comparing(ZipEntry::getName));
            for (var entry : entries) {
                if (entry.isDirectory()
                        || entry.getName().endsWith(".class")
                        || "META-INF/MANIFEST.MF".equals(entry.getName())) {
                    continue;
                }
                var target = new ZipEntry(entry.getName());
                target.setTime(0);
                output.putNextEntry(target);
                try (var stream = input.getInputStream(entry)) {
                    stream.transferTo(output);
                }
                output.closeEntry();
            }
        }
    }

    private static String visitGroups(String source) {
        source = replaceOnce(source, "    aVisitor.onUnknownRule (aUnknownRule);", """
                aVisitor.onUnknownRule (aUnknownRule);
                final CascadingStyleSheet parsed = aUnknownRule.getParsedRules ();
                if (parsed != null)
                  for (final ICSSTopLevelRule child : parsed.getAllRules ())
                    visitTopLevelRule (child, aVisitor);
            """.stripTrailing());
        return replaceOnce(source, "    if (aTopLevelRule instanceof CSSStyleRule)", """
            if (aTopLevelRule instanceof CSSNestedDeclarations)
            {
              visitNestedDeclarations ((CSSNestedDeclarations) aTopLevelRule, aVisitor);
              return;
            }
            if (aTopLevelRule instanceof CSSStyleRule)\
            """.stripTrailing());
    }

    private static String nestedGroups(String source) {
        source = replaceOnce(
                source,
                "  final public CSSNode styleDeclarationListWithNested() throws ParseException {",
                """
                private int styleDepth;

                public CSSNode nestedGroupBody() throws ParseException {
                  final CSSNode body = styleDeclarationListWithNested();
                  jj_consume_token(EOF);
                  return body;
                }

                final public CSSNode styleDeclarationListWithNested() throws ParseException {
                  styleDepth++;
                  try {
                    return styleBlockContents();
                  } finally {
                    styleDepth--;
                  }
                }

                // A type selector followed by a pseudo can start like a declaration.
                private boolean startsDeclaration() {
                  if (!jj_2_21(2147483647)) return false;
                  Token next = getToken(1);
                  if (styleDepth != 0 && next.image.startsWith("--")) return true;
                  int depth = 0;
                  for (;;) {
                    final int kind = next.kind;
                    if (kind == EOF) return true;
                    if (depth == 0) {
                      if (kind == LBRACE) return false;
                      if (kind == SEMICOLON || kind == RBRACE) return true;
                    }
                    if (kind == LSQUARE || kind == LROUND || kind >= FUNCTION_CALC && kind <= FUNCTION) depth++;
                    if (kind == RSQUARE || kind == RROUND) depth--;
                    if (next.next == null) next.next = token_source.getNextToken();
                    next = next.next;
                  }
                }

                private CSSNode styleBlockContents() throws ParseException {\
                """.stripTrailing());
        source = replaceOnce(source, "    if (jj_2_21(2147483647)) {", "    if (startsDeclaration()) {");
        source = replaceOnce(source, "  final public void styleRule() throws ParseException {", """
              final public void styleRule() throws ParseException {
                if (styleDepth == 0 && startsDeclaration()) {
                  final ParseException error = new ParseException("CSS declaration requires an enclosing style rule");
                  error.currentToken = token;
                  throw error;
                }
            """.stripTrailing());
        int media = source.indexOf("        label_122:");
        int mediaBody =
                source.indexOf("        switch (jj_ntk", source.indexOf("          jj_consume_token(S);", media));
        if (media < 0 || mediaBody < media || mediaBody > source.indexOf("          mediaRuleList();", media)) {
            throw new IllegalArgumentException("Pinned ph-css media body changed");
        }
        source = source.substring(0, mediaBody)
                + "        if (styleDepth != 0) styleDeclarationListWithNested();\n        else\n"
                + source.substring(mediaBody);
        source = replaceOnce(source, "public void layerBody() throws ParseException {", """
            public void layerBody() throws ParseException {
              if (styleDepth != 0) {
                styleDeclarationListWithNested();
                return;
              }\
            """.stripTrailing());
        return replaceOnce(source, "public void supportsRuleBody() throws ParseException {", """
            public void supportsRuleBody() throws ParseException {
              if (styleDepth != 0) {
                styleDeclarationBlockWithNested();
                return;
              }\
            """.stripTrailing());
    }

    private static String propertyComments(String source) {
        source = replaceOnce(
                source,
                "    if (m_aCustomErrorHandler != null)\n"
                        + "      m_aCustomErrorHandler.onIllegalCharacter (image.charAt (0));",
                """
                    if (image.length() == 1 && image.charAt(0) == '<') {
                      matchedToken.kind = LESS;
                      break;
                    }
                    if (m_aCustomErrorHandler != null)
                      m_aCustomErrorHandler.onIllegalCharacter (image.charAt (0));
                """.stripTrailing());
        return replaceOnce(source, "      curChar = input_stream.beginToken();", """
                  curChar = input_stream.beginToken();
                  if (curLexState == IN_PROPERTY_RULE && curChar == '/') {
                    final int next = input_stream.readChar();
                    if (next == '*') {
                      int previous = 0;
                      for (;;) {
                        final int character = input_stream.readChar();
                        if (previous == '*' && character == '/') break;
                        previous = character;
                      }
                      continue EOFLoop;
                    }
                    input_stream.backup (1);
                  }
            """.stripTrailing());
    }

    private static String conditionalChildren(String source) {
        source = replaceOnce(
                source,
                "final boolean bIsTopLevel)\n"
                        + "  {\n"
                        + "    _expectNodeType (aNode, ECSSNodeType.STYLEDECLARATIONLISTWITHNESTED);",
                "final boolean bIsTopLevel, @Nullable final Consumer <CSSPropertyRule> aGroupPropertyConsumer)"
                        + "\n  {\n    _expectNodeType (aNode, ECSSNodeType.STYLEDECLARATIONLISTWITHNESTED);");
        source = replaceOnce(
                source,
                "CSSNestedDeclarations aNestedDeclarations = null;",
                "CSSNestedDeclarations aNestedDeclarations = aGroupPropertyConsumer != null ? new"
                        + " CSSNestedDeclarations () : null;");
        source = replaceOnce(
                source,
                "_readStyleDeclarationListWithNestedRules (aChildNode, ret::addDeclaration, ret::addRule,"
                        + " bIsTopLevel);",
                "_readStyleDeclarationListWithNestedRules (aChildNode, ret::addDeclaration, ret::addRule,"
                        + " bIsTopLevel, null);");
        // Keep property registrations visible when a group uses the mixed declaration and rule production.
        source = replaceOnce(
                source,
                "      // else\n      // ignore ERROR_SKIP to and all unsupported nested \"@\" rules",
                """
                            else
                              if (aGroupPropertyConsumer != null && ECSSNodeType.PROPERTYRULE.isNode (aChildNode))
                              {
                                if (aNestedDeclarations.hasDeclarations ())
                                  aNestedRuleConsumer.accept (aNestedDeclarations);
                                aGroupPropertyConsumer.accept (_createPropertyRule (aChildNode));
                                aNestedDeclarations = new CSSNestedDeclarations ();
                              }
                // else
                // ignore ERROR_SKIP to and all unsupported nested "@" rules\
                """.stripTrailing());
        source = replaceOnce(source, "  private void _recursiveFillCascadingStyleSheetFromNode", """
            private void _readNestedGroupBody (final CSSNode body, final AbstractHasTopLevelRules group)
            {
              _readStyleDeclarationListWithNestedRules (body, declaration -> {
                throw new IllegalStateException ("Expected a nested declaration");
              }, rule -> group.addRule ((ICSSTopLevelRule) rule), false, group::addRule);
            }

            private void _recursiveFillCascadingStyleSheetFromNode\
            """.stripTrailing());
        source = replaceOnce(source, "    _expectNodeType (aNode, ECSSNodeType.ROOT);", """
            if (ECSSNodeType.STYLEDECLARATIONLISTWITHNESTED.isNode (aNode))
            {
              _readNestedGroupBody (aNode, ret);
              return;
            }
            _expectNodeType (aNode, ECSSNodeType.ROOT);\
            """.stripTrailing());
        for (var kind : new String[] {"media", "supports", "layer"}) {
            String variable = "layer".equals(kind) ? "aBodyChildNode" : "aChildNode";
            int production = source.indexOf("Unsupported " + kind + "-rule child:");
            int firstStyle = source.lastIndexOf("if (ECSSNodeType.STYLERULE.isNode (" + variable + "))", production);
            if (production < 0 || firstStyle < 0) {
                throw new IllegalArgumentException("Pinned ph-css group body conversion changed");
            }
            source = source.substring(0, firstStyle)
                    + "if (ECSSNodeType.STYLEDECLARATIONLISTWITHNESTED.isNode (" + variable + "))\n"
                    + "  _readNestedGroupBody (" + variable + ", ret);\nelse "
                    + source.substring(firstStyle);
            int marker = source.indexOf("\"Unsupported " + kind + "-rule child:");
            String check = "if (!ECSSNodeType.isErrorNode (" + variable + "))";
            int offset = source.lastIndexOf(check, marker);
            if (marker < 0 || offset < 0) {
                throw new IllegalArgumentException("Pinned ph-css conditional conversion changed");
            }
            String branches = "if (ECSSNodeType.PROPERTYRULE.isNode (" + variable + "))\n"
                    + "  ret.addRule (_createPropertyRule (" + variable + "));\nelse ";
            if (!"media".equals(kind)) {
                branches += "if (ECSSNodeType.UNKNOWNRULE.isNode (" + variable + "))\n"
                        + "  ret.addRule (_createUnknownRule (" + variable + "));\nelse ";
            }
            source = source.substring(0, offset) + branches + source.substring(offset);
        }
        return source;
    }

    private static String structuredRule(String source) {
        source = replaceOnce(source, "  private String m_sBody;", """
              private String m_sBody;
              @Nullable
              private CascadingStyleSheet m_aParsedRules;

              @Nullable
              public CascadingStyleSheet getParsedRules () { return m_aParsedRules; }

              public void setParsedRules (@NonNull final CascadingStyleSheet rules) {
                m_aParsedRules = rules;
              }
            """.stripTrailing());
        return replaceOnce(source, "    // Always ignore unknown rules?", """
                if (m_aParsedRules != null) {
                  final StringBuilder result = new StringBuilder (m_sDeclaration);
                  if (m_sParameterList != null) result.append (" ").append (m_sParameterList);
                  result.append ("{");
                  for (final ICSSTopLevelRule child : m_aParsedRules.getAllRules ())
                    result.append (child.getAsCSSString (aSettings, nIndentLevel + 1));
                  return result.append ("}").toString ();
                }
                // Always ignore unknown rules?
            """.stripTrailing());
    }

    private static String repair(String source) {
        source = source.replace(
                "    throw (Error)jjte000;",
                "    if (jjte000 instanceof RuntimeException) throw (RuntimeException)jjte000;\n"
                        + "    throw (Error)jjte000;");
        int first = source.indexOf("public void expr()");
        int last = source.indexOf("// Charset rule", first);
        if (first < 0 || last < first) {
            throw new IllegalArgumentException("Pinned ph-css expression production changed");
        }
        String expression = source.substring(first, last);
        expression = replaceOnce(
                expression,
                "        switch (jj_ntk == -1 ? jj_ntk_f() : jj_ntk) {\n        case COMMA:",
                "        boolean comma = false;\n"
                        + "        switch (jj_ntk == -1 ? jj_ntk_f() : jj_ntk) {\n        case COMMA:");
        expression = replaceOnce(
                expression,
                "          exprOperator();",
                "          comma = getToken(1).kind == COMMA;\n          exprOperator();");
        expression = replaceOnce(
                expression,
                "        exprTerm();",
                "        if (comma && getToken(1).kind == RROUND) { break label_27; }\n        exprTerm();");
        source = source.substring(0, first) + expression + source.substring(last);
        first = source.indexOf("public void layerBody()");
        last = source.indexOf("public void layerRuleBlock()", first);
        if (first < 0 || last < first) {
            throw new IllegalArgumentException("Pinned ph-css layer production changed");
        }
        String layer = source.substring(first, last);
        layer = replaceOnce(
                layer,
                "public void layerBody() throws ParseException {",
                "public void layerBody() throws ParseException {\n    if (getToken(1).kind == RBRACE) return;");
        layer = replaceOnce(
                layer,
                "      case PROPERTY_SYM:{",
                "      case AT_UNKNOWN:{ unknownRule(); break; }\n      case PROPERTY_SYM:{");
        layer = replaceOnce(
                layer,
                "      case PROPERTY_SYM:\n      case IDENT:",
                "      case PROPERTY_SYM:\n      case AT_UNKNOWN:\n      case IDENT:");
        layer = replaceOnce(
                layer,
                "errorUnexpectedRule (\"@property\", \"property rule in the middle of a @layer rule is not"
                        + " allowed!\");",
                "");
        return source.substring(0, first) + layer + source.substring(last);
    }

    private static String replaceOnce(String source, String original, String replacement) {
        int offset = source.indexOf(original);
        if (offset < 0 || source.indexOf(original, offset + original.length()) >= 0) {
            throw new IllegalArgumentException("Pinned ph-css expression production differs");
        }
        return source.substring(0, offset) + replacement + source.substring(offset + original.length());
    }
}
