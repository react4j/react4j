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

/** Extends the pinned grammar and opaque-rule model for processed modern CSS. */
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
                    case "com/helger/css/parser/ParserCSS30.java" -> repair(source);
                    case "com/helger/css/parser/ParserCSS30TokenManager.java" -> propertyComments(source);
                    case "com/helger/css/decl/CSSUnknownRule.java" -> structuredRule(source);
                    case "com/helger/css/handler/CSSNodeToDomainObject.java" -> conditionalChildren(source);
                    case "com/helger/css/decl/visit/CSSVisitor.java" ->
                        replaceOnce(source, "    aVisitor.onUnknownRule (aUnknownRule);", """
                                aVisitor.onUnknownRule (aUnknownRule);
                                final CascadingStyleSheet parsed = aUnknownRule.getParsedRules ();
                                if (parsed != null)
                                  for (final ICSSTopLevelRule child : parsed.getAllRules ())
                                    visitTopLevelRule (child, aVisitor);
                            """.stripTrailing());
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

    private static String propertyComments(String source) {
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
        for (var kind : new String[] {"media", "supports", "layer"}) {
            String variable = "layer".equals(kind) ? "aBodyChildNode" : "aChildNode";
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
