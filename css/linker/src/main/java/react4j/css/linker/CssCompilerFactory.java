package react4j.css.linker;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import react4j.css.internal.CssLink;

/** Integrates CSS value liveness with the stock Closure worker. */
public final class CssCompilerFactory implements CompilerFactory {
    @Override
    public Compiler create(Path configuration, Path report) throws IOException {
        var link = CssLink.read(Files.readString(configuration));
        if (!link.optimized()) {
            Files.writeString(report, link.report(Set.of()));
            return new Compiler();
        }
        return new StyleCompiler(link, report);
    }
}
