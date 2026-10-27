package react4j.examples.input_semantics;

import java.util.Locale;
import react4j.InputKeyGenerator;

public final class LowerCaseKeys implements InputKeyGenerator<String> {
    @Override
    public String getKey(final String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
