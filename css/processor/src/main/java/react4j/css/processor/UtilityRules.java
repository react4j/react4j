package react4j.css.processor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import react4j.css.utility.language.UtilityLanguage;

/** Explicit-file JVM authoring commands, using the same checked facade as javac. */
public final class UtilityRules {
    private static final Gson GSON = new Gson();

    private UtilityRules() {}

    private static final class Usage extends IllegalArgumentException {
        Usage(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        int result = run(args, System.out);
        if (0 != result) {
            System.exit(result);
        }
    }

    static int run(String[] raw, PrintStream output) {
        var args = Arrays.asList(raw);
        boolean text = !args.isEmpty() && "--text".equals(args.get(0));
        if (text) {
            args = args.subList(1, args.size());
        }
        var response = new JsonObject();
        try {
            if (args.size() < 2 || !List.of("validate", "describe", "expand").contains(args.get(0))) {
                throw new Usage(
                        "Usage: UtilityRules [--text] validate|describe rules.css [provider.checked.json]; expand"
                                + " provider.checked.json candidate [...]");
            }
            String command = args.get(0);
            if ("expand".equals(command)) {
                if (args.size() < 3) {
                    throw new Usage("expand requires at least one explicit candidate");
                }
                var library = UtilityLanguage.readChecked(Files.readString(Path.of(args.get(1))));
                var batch = UtilityLanguage.expand(library, args.subList(2, args.size()));
                response.addProperty("status", batch.diagnostics().isEmpty() ? "checked" : "failed");
                response.addProperty("provider", library.owner());
                response.addProperty("fingerprint", library.fingerprint());
                response.add("expansions", GSON.toJsonTree(batch.expansions()));
                response.add("diagnostics", GSON.toJsonTree(batch.diagnostics()));
                if (text) {
                    for (var expansion : batch.expansions()) {
                        output.println(
                                expansion.candidate() + " -> " + library.owner() + "#CLASS:" + expansion.candidate());
                        output.println(expansion.css());
                        output.println(GSON.toJson(expansion.trace()));
                    }
                    batch.diagnostics().forEach(diagnostic -> output.println(diagnostic.render()));
                } else {
                    output.println(response);
                }
                return batch.diagnostics().isEmpty() ? 0 : 1;
            }
            if (args.size() > 3) {
                throw new Usage(command + " accepts a source and optional provider snapshot");
            }
            var path = Path.of(args.get(1));
            var prepared = UtilityLanguage.prepare(new UtilityLanguage.Source(path.toString(), Files.readString(path)));
            response.addProperty("status", 3 == args.size() ? "checked" : "prepared");
            if (3 == args.size()) {
                var snapshot = UtilityLanguage.readChecked(Files.readString(Path.of(args.get(2))));
                var checked = UtilityLanguage.bind(prepared, snapshot.owner(), snapshot.bindings());
                response.addProperty("provider", checked.owner());
                response.addProperty("fingerprint", checked.fingerprint());
                response.add("library", JsonParser.parseString(checked.json()));
            } else {
                var pending = new JsonArray();
                pending.add("Provider ownership, imports and external hooks require a checked provider snapshot");
                response.add("pendingChecks", pending);
                response.add("library", JsonParser.parseString(prepared.json()));
            }
            response.add("diagnostics", new JsonArray());
            if ("describe".equals(command)) {
                response.add("scales", GSON.toJsonTree(prepared.scales()));
                response.add("tokens", GSON.toJsonTree(prepared.tokens()));
                response.add("utilities", GSON.toJsonTree(prepared.utilities()));
                response.add("variants", GSON.toJsonTree(prepared.variants()));
            }
            if (text) {
                output.println(response.get("status").getAsString() + ": " + path);
                if (response.has("provider")) {
                    output.println(response.get("provider").getAsString() + " "
                            + response.get("fingerprint").getAsString());
                }
                if (response.has("pendingChecks")) {
                    output.println(response.get("pendingChecks")
                            .getAsJsonArray()
                            .get(0)
                            .getAsString());
                }
                if ("describe".equals(command)) {
                    output.println("Scales: " + prepared.scales());
                    for (var group : prepared.tokens()) {
                        output.println(group.name() + " <" + group.type() + "> " + group.values());
                    }
                    for (var rule : Stream.concat(prepared.utilities().stream(), prepared.variants().stream())
                            .toList()) {
                        output.println(
                                rule.name() + " " + rule.sources() + " " + rule.interval() + "\n" + rule.template());
                    }
                }
            } else {
                output.println(response);
            }
            return 0;
        } catch (UtilityLanguage.Failure e) {
            response.addProperty("status", "failed");
            response.add("diagnostics", GSON.toJsonTree(List.of(e.diagnostic())));
            output.println(text ? e.diagnostic().render() : response);
            return 1;
        } catch (IOException | RuntimeException e) {
            response.addProperty("status", "failed");
            var diagnostic = new JsonObject();
            diagnostic.addProperty(
                    "code", e instanceof IOException ? "U_INPUT" : e instanceof Usage ? "U_USAGE" : "U_DESCRIPTOR");
            diagnostic.addProperty("message", String.valueOf(e.getMessage()));
            var diagnostics = new JsonArray();
            diagnostics.add(diagnostic);
            response.add("diagnostics", diagnostics);
            output.println(text ? diagnostic.get("code").getAsString() + ": " + e.getMessage() : response);
            return 2;
        }
    }
}
