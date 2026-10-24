package react4j.css.processor;

import react4j.css.internal.CssIdentity;

final class Names {
    private Names() {}

    static String encode(String name) {
        return CssIdentity.encode(name);
    }

    static String root(String binaryName) {
        return CssIdentity.root(binaryName);
    }
}
