package react4j.site;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.jspecify.annotations.NullMarked;
import react4j.browser.Preview;

@NullMarked
public final class Main {
    private Main() {}

    public static void main(final String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println(
                    "Usage: site build <inputs> | check <directory> | serve <directory> | prepare-deploy <site>"
                            + " <checkout>");
        } else if (args.length == 3 && args[0].equals("prepare-deploy")) {
            SiteLinks.check(Path.of(args[1]));
            SiteDeploy.prepare(Path.of(args[1]), Path.of(args[2]));
        } else if (args.length == 2 && args[0].equals("check")) {
            SiteLinks.check(Path.of(args[1]));
        } else if (args.length == 2 && args[0].equals("serve")) {
            Preview.serve(Path.of(args[1]));
        } else if (args.length > 1 && args[0].equals("build")) {
            final var files = new LinkedHashMap<String, Path>();
            final var docs = new ArrayList<Path>();
            final var sources = new ArrayList<Path>();
            final var assets = new LinkedHashMap<Path, String>();
            for (int i = 1; i < args.length; i += 2) {
                if (i + 1 == args.length) {
                    throw new IllegalArgumentException("Missing value for " + args[i]);
                }
                final var value = args[i + 1];
                switch (args[i]) {
                    case "--doc" -> docs.add(Path.of(value));
                    case "--source-jar" -> sources.add(Path.of(value));
                    case "--asset" -> {
                        final var separator = value.indexOf('=');
                        if (separator < 1) {
                            throw new IllegalArgumentException("Expected asset source=destination: " + value);
                        }
                        assets.put(Path.of(value.substring(0, separator)), value.substring(separator + 1));
                    }
                    case "--output", "--layout", "--home", "--document", "--navigation", "--api" ->
                        files.put(args[i], Path.of(value));
                    default -> throw new IllegalArgumentException("Unknown argument: " + args[i]);
                }
            }
            for (final var required :
                    new String[] {"--output", "--layout", "--home", "--document", "--navigation", "--api"}) {
                if (!files.containsKey(required)) {
                    throw new IllegalArgumentException("Missing " + required);
                }
            }
            SiteBuilder.build(files, docs, sources, assets);
        } else {
            throw new IllegalArgumentException("Usage: site build <inputs> | check <directory> | serve <directory>");
        }
    }
}
